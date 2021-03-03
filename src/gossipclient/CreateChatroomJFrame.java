package gossipclient;

import org.json.*;
import java.net.*;
import java.io.IOException;

/**
 * La classe implementa il task di inviare al server
 * la richiesta di creazione di una chatroom
 * @author giuli
 */
public class CreateChatroomJFrame extends javax.swing.JFrame {
    
    /**
     * Metodo costruttore
     */
    public CreateChatroomJFrame() {
         initComponents();
    }
    
    private void initComponents() {
        
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        createChatroom = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 102, 255));

        createChatroom.setText("Crea Chatroom!");
        createChatroom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addComponent(createChatroom)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createChatroom))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {
	
        this.chatname = jTextField1.getText(); //nome della chatroom da creare
	JSONObject msgObj = new JSONObject();
	MulticastSocket ms = LoginGossipClient.msSocket;
	
        //invio della richiesta
        try {
	    msgObj.put("OP","CREATE");
            msgObj.put("username", LoggedUserFrame.username);
            msgObj.put("groupname", chatname);
            ClientMainJFrame.writeRequest(msgObj.toString());
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        
        //lettura della risposta
        String reply = ClientMainJFrame.readReply();
        String replycode = null;
        try {
            JSONObject replyObj = new JSONObject(reply);
            replycode = replyObj.getString("replycode");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        
        //se esiste già una chatroom con questo nome
        if(replycode.equals(ReplyType.CHATALREADY.name())) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Esiste già una chatroom con questo nome");
            popUpMessage.setVisible(true);
            return;
        }
        
        //se la richiesta è andata a buon fine
        else if(replycode.equals(ReplyType.OK.name())) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Chatroom creata con successo");
            popUpMessage.setVisible(true);     
            InetAddress msAddr;
            
            //ricezione di un messaggio del server contenente l'indirizzo della chatroom
            String rep = ClientMainJFrame.readReply();
            JSONObject addrObj;
            StringBuilder sb = null;
            try {
                addrObj = new JSONObject(rep);
                sb = new StringBuilder(addrObj.getString("address"));
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            
            //eliminazione del primo carattere della stringa (un "/")
            sb.deleteCharAt(0);
            String addr = new String(sb);
            try {
                msAddr = InetAddress.getByName(addr);
            	ms.joinGroup(msAddr);
            }
            catch(UnknownHostException e) {
                e.printStackTrace();
            }
            catch(IOException e) {  //problema di I/0 sul multicast socket, terminazione del client
                LogoutTask logOut = new LogoutTask();
                Thread logOutThread = new Thread(logOut);
                logOutThread.start();
            }
        }
    }
    
    private String chatname;
    private javax.swing.JButton createChatroom;
    private javax.swing.JPanel jPanel1;
    private static javax.swing.JTextField jTextField1;
}
