package gossipclient;

import java.io.*;
import java.rmi.*;
import org.json.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;
import java.awt.event.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import gossipserver.ServerRemoteInterface;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe che implementa il frame dedicato ad un utente
 * che ha fatto il login
 * @author Giulia Fois, Laura Bussi
 */
public class LoggedUserFrame extends javax.swing.JFrame {
    
    
    static String username; //username dell'utente
    //path della directory dell'utente, che conterrà i files da lui ricevuti
    static String filesDirPath; 
    int RMIPort = 6000; //porta riservata al servizio RMI
    static Vector<String> log; //log degli eventi degli amici
    static int msgIn; //porta per i messaggi in ingresso
    static int msgOut; //porta per i messaggi in uscita
    static Socket msgLisSock; //socket per i messaggi in ingresso
    static Socket msgWriSock; //socket per i messaggi in uscita
    static BufferedReader msgReader; //lettore dei messaggi in ingresso
    static BufferedWriter msgWriter; //scrittore dei messaggi in uscita
    static BufferedReader msgReplyReader; //lettore delle risposte ai messaggi del server
    //frames attivi delle chatroom
    static ConcurrentHashMap<String, ChatroomJFrame> chatroomFrames;
    //frames attivi delle chat amici
    static ConcurrentHashMap<String, FriendChatJFrame> chatFrames;
    static ServerRemoteInterface server; //interfaccia remota del server su cui registrarsi
    static ClientRemoteInterface stub;
    
    /**
     * Metodo Costruttore
     * @param sock Socket per i messaggi in entrata (aperto in fase di login)
     * @param username Username dell'utente
     * @param msgIn Porta per i messaggi in ingresso (????)
     * @param msgOut Porta del server per i messaggi in uscita
     */
    public LoggedUserFrame(Socket sock, String username, int msgIn, int msgOut) {
        
        initComponents();
        
        this.msgOut = msgOut;
        this.msgLisSock = sock;
        this.username = username;
        log = new Vector<String>();
        chatFrames = new ConcurrentHashMap<String,FriendChatJFrame>();
        chatroomFrames = new ConcurrentHashMap<String,ChatroomJFrame>();
        
        //spawn del thread per aggiornamento liste amici e chatroom
        ClientListUpdater cfu = new ClientListUpdater();
        Thread newThread = new Thread(cfu);
        newThread.start();
        
        
        try {
            //apertura del socket per i messaggi in uscita
            msgWriSock = new Socket(ClientMainJFrame.serverAddr, msgOut);
            
            //apertura dei lettori e scrittori sui socket
            msgReader = new BufferedReader(new InputStreamReader(msgLisSock.getInputStream()));
            msgWriter = new BufferedWriter(new OutputStreamWriter(msgWriSock.getOutputStream()));
            msgReplyReader = new BufferedReader(new InputStreamReader(msgWriSock.getInputStream()));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        
        //spawn del thread listener di messaggi in entrata
        ClientListener lis = new ClientListener(msgLisSock);
        Thread threadLis = new Thread(lis);
        threadLis.start();
        
        //spawn del thread listener dei messaggi multicast
        ClientMulticastManager cm = new ClientMulticastManager(LoginGossipClient.msSocket);
        Thread tcm = new Thread(cm);
        tcm.start();
        
        //registrazione al servizio RMI
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPort);
            server = (ServerRemoteInterface) registry.lookup("RemoteServer");
            ClientRemoteInterface callbackObj = new ClientRemoteImpl();
            stub = (ClientRemoteInterface) UnicastRemoteObject.exportObject(callbackObj,0);
            server.register(username, stub);
        } 
        catch (NotBoundException | RemoteException ex) {
            ex.printStackTrace();
        } 
        
        //creazione della directory specifica per i file dell'utente
        filesDirPath = "./" + username + "'s files";
        if(!Files.exists(Paths.get(filesDirPath)))
            new File(filesDirPath).mkdir();
        
        jLabel7.setText("Benvenuto, " + username + "!");
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jButton3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 102, 255));

        jLabel1.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Social Gossip");

        jLabel11.setFont(new java.awt.Font("Magneto", 3, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("L&G");

        jButton1.setText("Cerca");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("cerca utente");

        jLabel4.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("seleziona un amico:");

        jLabel5.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 11)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("seleziona una chatroom:");

        jScrollPane1.setViewportView(jList1);

        jScrollPane2.setViewportView(jList2);

        jButton3.setText("LOGOUT");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton3MouseReleased(evt);
            }
        });

        jScrollPane3.setViewportView(jList3);

        jLabel6.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("log attività amici:");

        jButton4.setText("Chatta!");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton4MouseReleased(evt);
            }
        });

        jButton5.setText("Chatta!");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton5MouseReleased(evt);
            }
        });

        jButton6.setText("Crea Chatroom");
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton6MouseReleased(evt);
            }
        });
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Chiudi Chatroom");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Microsoft New Tai Lue", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));

        jButton8.setText("Partecipa alla Chatroom");
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton8MouseReleased(evt);
            }
        });
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton7)
                                .addGap(22, 22, 22))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addComponent(jButton4)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(jButton5))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton8))))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(175, 175, 175)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton1))
                                    .addComponent(jLabel3))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(196, 196, 196)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(106, 121, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addGap(23, 23, 23)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton4)
                            .addComponent(jButton5))
                        .addGap(18, 18, 18)
                        .addComponent(jButton8)
                        .addGap(11, 11, 11)
                        .addComponent(jButton7)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Metodo che gestisce la pressione del bottone "Chiudi Chatroom"
     * per la chiusura di una Chatroom
     * @param evt Evento da gestire
     */
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        /*
        chatroom da chiudere, selezionata dalla lista, con il ruolo dell'utente
        rispetto ad essa (cratore, iscritto o nulla)
        */
        String group = jList2.getSelectedValue();
        if(group != null) { //se la selezione è andata a buon fine
            
            //controllo che l'utente sia il creatore della chatroom
            String[] tokens = group.split(" "); //per estrarre il nome della chatroom
            if(tokens.length == 1 || !tokens[1].equals("(creator)")) {
                PopUpJFrame popUpMessage = new PopUpJFrame("Non puoi chiudere una chatroom di cui non sei il creatore");
                popUpMessage.setVisible(true);
                return;
            }
            
            JSONObject reqObj = new JSONObject();
            JSONObject replyObj;
            
            try {
                //invio della richiesta di chiusura al server
                reqObj.put("OP", RequestType.CLOSECHAT);
                reqObj.put("username", LoggedUserFrame.username);
                reqObj.put("groupname", tokens[0]);
                ClientMainJFrame.writeRequest(reqObj.toString());
                
                //lettura della risposta
                String reply = ClientMainJFrame.readReply();
                replyObj = new JSONObject(reply);
                
                //se la chiusura non è andata a buon fine
                if(!replyObj.getString("replycode").equals(ReplyType.OK.name())) {
                    PopUpJFrame popUpFrame = new PopUpJFrame("C'è stato un errore con la chiusura della chatroom");
                    popUpFrame.setVisible(true);
                    return;
                }
                else {
                    PopUpJFrame popUpFrame = new PopUpJFrame("La chatroom " + tokens[0] + " è stata chiusa");
                    popUpFrame.setVisible(true);
                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed
    
    /**
     * Metodo che gestisce la pressione del bottone "Crea Chatroom"
     * per la creazione di una chatroom
     * @param evt Evento da gestire 
     */
    private void jButton6MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseReleased
        
        //creazione del frame per la creazione della chatroom
        CreateChatroomJFrame crChat = new CreateChatroomJFrame();
        crChat.setVisible(true);
    }//GEN-LAST:event_jButton6MouseReleased
    
    /**
     * Metodo che gestisce la pressione del bottone "Chatta!" per
     * iniziare a chattare con i membri di una chatroom
     * @param evt Evento da gestire
     */
    private void jButton5MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseReleased
        /*
        stringa selezionata dalla lista, che contiene il nome
        della chatroom e il ruolo che l'utente ricopre in esso
        (creatore, iscritto o non ancora iscritto)
        */
        final String group = jList2.getSelectedValue();
        
        if(group != null) { //la selezione è andata a buon fine
            
            //per estrarre i campi dalla stringa selezionata
            String[] tokens = group.split(" ");
            if(tokens.length == 1) { //è presente solo il nome della chatroom: l'utente non è iscritto
                PopUpJFrame popUpMessage = new PopUpJFrame("Non sei ancora iscritto a questa chatroom");
                popUpMessage.setVisible(true);
                return;
            }
            //l'utente è iscritto alla chatroom: creazione del frame per essa
            ChatroomJFrame chat = new ChatroomJFrame(tokens[0]);
            chat.setVisible(true);
            
            //aggiunta del frame alla lista di quelli attivi
            addChatroomFrame(tokens[0], chat);
            chat.addWindowListener(new WindowAdapter() {
                /*
                handler che rimuove il frame dalla lista di quelli attivi
                alla sua chiusura
                */
                @Override
                public void windowClosing(WindowEvent evt) {
                    removeChatroomFrame(tokens[0]);
                }
            });
        }
    }//GEN-LAST:event_jButton5MouseReleased
    
    /**
     * Metodo che gestisce la pressione del bottone "Chatta!" per
     * iniziare a chattare con un amico
     * @param evt Evento da gestire
     */
    private void jButton4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseReleased
        
        //utente selezionato con indicato il suo status
        String selected = jList1.getSelectedValue();
        if(selected != null) { //se la selezione è andata a buon fine
            String[] parts = selected.split(" "); //per esrrarre nome e status dell'utente
            String selectedFriend = parts[0];
            String selectedFriendStatus = parts[1];
            if(selectedFriendStatus.equals("OFF")) { //l'amico selezionato è offline
                PopUpJFrame popUpMessage = new PopUpJFrame("Non puoi chattare con un utente offline");
                popUpMessage.setVisible(true);
                return;
            }
            else { //l'amico selezionato è online
                FriendChatJFrame chat = new FriendChatJFrame(selectedFriend);
                chat.setVisible(true);
                
                //aggiunta del frame alla lista di quelli attivi
                addFriendChatFrame(selectedFriend, chat);
                chat.addWindowListener(new WindowAdapter() {
                    /*
                    handler che rimuove il frame dalla lista di quelli attivi
                    alla sua chiusura
                    */
                    @Override
                    public void windowClosing(WindowEvent evt) {
                        removeFriendChatFrame(selectedFriend);
                    }
                });
                
            }
        }
    }//GEN-LAST:event_jButton4MouseReleased
    
    /**
     * Metodo che gestisce la pressione del bottone "Logout" per
     * il logout dell'utente
     * @param evt 
     */
    private void jButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseReleased
        //spawn del thread che gestisce il logout
        LogoutTask logOut = new LogoutTask();
        Thread logoutThread = new Thread(logOut);
        logoutThread.start();
    }//GEN-LAST:event_jButton3MouseReleased
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

//GEN-FIRST:event_jButton1MouseReleased
 
//GEN-LAST:event_jButton1MouseReleased
    /**
     * Metodo per la gestione della pressione del bottone "Partecipa alla Chatroom"
     * per la registrazione ad una chatroom
     * @param evt Evento da gestire 
     */
    private void jButton8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseReleased
        
        JSONObject req = new JSONObject();
        /*
        nome della chatroom a cui l'utente si vuole iscrivere e ruolo dell'utente
        nella chatroom (creatore, iscritto o non iscritto)
        */
        String selectedGroup = jList2.getSelectedValue();
        
        if(selectedGroup != null) { //se la selezione è andata a buon fine
            String[] tokens = selectedGroup.split(" "); //per estrarre il nome della chatroom
            if(tokens.length == 1) { //non è ancora iscritto al gruppo
                try {
                    
                    //invio della richiesta di iscrizione
                    req.put("username", LoggedUserFrame.username);
                    req.put("OP", RequestType.ADDME);
                    req.put("groupname", tokens[0]);  
                    ClientMainJFrame.writeRequest(req.toString());
                    
                    //ricezione della risposta
                    String reply = ClientMainJFrame.readReply();
                   JSONObject replyObj = new JSONObject(reply);
                    MulticastSocket socket = LoginGossipClient.msSocket;
                    
                    //ricezione dell'indirizzo della chatroom
                    String rep = ClientMainJFrame.readReply();
         	    JSONObject addrObj = new JSONObject(rep);
                    
                    //registrazione alla chatroom
         	    StringBuilder sb = new StringBuilder(addrObj.getString("address"));
         	    sb.deleteCharAt(0); //per eliminare il carattere "/" all'inizio della stringa
         	    String addr = new String(sb);
         	    InetAddress msAddr = InetAddress.getByName(addr);
         	    socket.joinGroup(msAddr);
                    
                    PopUpJFrame popUpMessage = new PopUpJFrame("Ti sei iscritto a " + selectedGroup + "!");
                    popUpMessage.setVisible(true);
         	}
                catch(JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
            else {
               PopUpJFrame popUpMessage = new PopUpJFrame("Sei già iscritto a questa chatroom");
               popUpMessage.setVisible(true);
            }
        }
    }//GEN-LAST:event_jButton8MouseReleased

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed
    
    /**
     * Metodo per la gestione della pressione del bottone "Cerca" per
     * la ricerca di un utente
     * @param evt Evento da gestire
     */
    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {                                       
        //nome dell'amico da cercare
        String user = jTextField1.getText();
        
        //spawn del thread che invia la richiesta al server
        LookupTask lt = new LookupTask(user, username);
        Thread t = new Thread(lt);
        t.start();
        
        //reset del campo di ricerca
        jTextField1.setText("");
    }                                      

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private static javax.swing.JLabel jLabel7;
    private static javax.swing.JList<String> jList1;
    private static javax.swing.JList<String> jList2;
    private static javax.swing.JList<String> jList3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
    

    /**
     * Metodo che aggiorna graficamente la lista degli amici
     * @param friends Vettore contenente la lista amici
     */
    public static void updateFriendList(Vector<String> friends) {
        //svuota il log della lista amici
        jList1.removeAll();
        jList1.invalidate();
        //carica la nuova lista amici
        jList1.setListData(friends);
        jList1.invalidate();
    }

    /**
     * Metodo che aggiorna graficamente il log degli eventi
     * degli amici
     * @param newEvent Evento da aggiungere al log
     */
    public synchronized static void updateLog(String newEvent) {
        //aggiunge il nuovo evento alla struttura del log
        log.add(newEvent);
        //carica il log sulla sua jList
        jList3.setListData(log);
        
    }
    
    /**
     * Metodo che aggiorna graficamente la lista delle chatroom
     * @param chatrooms Vettore contenente la lista delle chatroom
     */
    public static synchronized void updateChatList(Vector<String> chatrooms) {
        //svuota il log della lista chatroom
        jList2.removeAll();
        
        //lo riempie con la nuova lista
        jList2.setListData(chatrooms);
        
    }
    
    /**
     * Metodo che legge un messaggio dal socket dei messaggi
     * in entrata
     * @return Il messaggio letto
     */
    public static String readMessage() {
        String message = null; //messaggio che verrà letto
            try {
                message = msgReader.readLine();
            } catch (IOException ex) {
                closeListenerSock();
            }
        return message;
    }
    
    /**
     * Scrive un messaggio sul socket dei messaggi
     * in entrata
     * @param msg Messaggio da scrivere
     */
    public static void writeMessage(String msg) {
        try {
            msgWriter.write(msg);
            msgWriter.newLine();
            msgWriter.flush();
        }
        //problema con la scrittura: chiusura del socket e terminazione
        catch(IOException e) { 
            closeWriterSock();
            LogoutTask logOut = new LogoutTask();
            Thread logOutThread = new Thread(logOut);
            logOutThread.start();
        }
    }
    
    /**
     * Legge la risposta ad un messaggio sullo stesso socket
     * su cui è stato mandato il messaggio
     * @return La risposta letta
     */
    public synchronized static String readMessageReply() {
        String reply = null; //risposta che verrà letta
        try {
            reply = msgReplyReader.readLine();
        }
        //problema con la lettura: chiusura del socket e terminazione
        catch (IOException ex) {
            closeWriterSock();
            LogoutTask logOut = new LogoutTask();
            Thread logOutThread = new Thread(logOut);
            logOutThread.start();
        }
        return reply;
    }

    /**
     * Metodo che aggiunge un frame della chat amici
     * alla hashmap di quelli attivi
     * @param key Chiave per la hashmap: nome dell'amico con cui
     *            l'utente sta chattando o vuole chattare
     * @param frame Valore per la hashmap: frame che si è attivato
     */
    public static void addFriendChatFrame(String key, FriendChatJFrame frame) {
        chatFrames.put(key, frame);
    }
    
    /**
     * Metodo che rimuove un frame della chat amici
     * dalla hashmap di quelli attivi
     * @param key Chiave per la hashmap: nome dell'amico con cui
     *            l'utente sta chattando o vuole chattare
     */
    public static void removeFriendChatFrame(String key) {
        chatFrames.remove(key);
    }
    
    /**
     * Metodo che controlla se un frame della chat amici è
     * attivo o meno
     * @param key Chiave della hashmap: nome dell'amico con cui
     *            l'utente sta chattando o vuole chattare
     * @return true se il frame è attivo, false altrimenti 
     */
    public static boolean containsFriendChatFrame(String key) {
        if(chatFrames.containsKey(key)) return true;
        else return false;
    }

    /**
     * Metodo che ritorna un frame della chat amici dalla 
     * hashmap di quelli attivi
     * @param key Chiave della hashmap: nome dell'amico con cui
     *            l'utente sta chattando o vuole chattare
     * @return Il frame corrispondente all'amico dell'utente
     */
    public static FriendChatJFrame getFriendChatFrame(String key) {
        return chatFrames.get(key);
    }
    
    /**
     * Metodo che aggiunge un frame delle chatroom
     * alla hashmap di quelli attivi
     * @param key Chiave per la hashmap: nome della chatroom in cui
     *            l'utente sta chattando o vuole chattare
     * @param frame Valore per la hashmap: frame che si è attivato
     */
    public static void addChatroomFrame(String key, ChatroomJFrame frame) {
        chatroomFrames.put(key, frame);
    }
    
     /**
     * Metodo che rimuove un frame delle chatroom
     * dalla hashmap di quelli attivi
     * @param key Chiave per la hashmap: nome della chatroom in cui
     *            l'utente sta chattando o vuole chattare
     */
    public static void removeChatroomFrame(String key) {
        if(containsChatroomFrame(key)) {
            chatroomFrames.remove(key);
        }
    }
    
    /**
     * Metodo che controlla se un frame delle chatroom è
     * attivo o meno
     * @param groupname Chiave della hashmap: nome della chatroom in
     *                  cui l'utente sta chattando o vuole chattare
     * @return true se il frame è attivo, false altrimenti 
     */
    public static boolean containsChatroomFrame(String groupname) {
        if(chatroomFrames.containsKey(groupname)) return true;
        else return false;
    }
    
    /**
     * Metodo che ritorna un frame delle chatroom dalla 
     * hashmap di quelli attivi
     * @param groupname Chiave della hashmap: nome della chatroom in
     *                  cui l'utente sta chattando o vuole chattare
     * @return Il frame corrispondente alla chatroom
     */
    public static ChatroomJFrame getChatroomFrame(String groupname) {
        return chatroomFrames.get(groupname);
    }
    
    /**
     * Metodo che chiude il socket dei messaggi in entrata
     */
    public static void closeListenerSock() {
        if(!msgLisSock.isClosed() && msgLisSock != null) {
            try {
                msgLisSock.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Metodo che chiude il socket dei messaggi in uscita
     */
    public static void closeWriterSock() {
        if(!msgWriSock.isClosed() && msgWriSock != null) {
            try {
                msgWriSock.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

    }
}