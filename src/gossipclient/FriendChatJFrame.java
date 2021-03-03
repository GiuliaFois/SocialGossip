package gossipclient;

import java.io.*;
import java.net.*;
import java.nio.*;
import org.json.*;
import java.nio.file.*;
import java.nio.channels.*;
import javax.swing.JFileChooser;

/**
 * La classe implementa il frame della chat amici
 * e le possibili operazioni su esso
 * @author Giulia Fois, Laura Bussi
 */
public class FriendChatJFrame extends javax.swing.JFrame {
    
    String receiver; //amico con cui l'utente sta chattando
    
    /**
     * Metodo costruttore
     * @param nickname Username dell'amico con cui l'utente sta chattando
     */
    public FriendChatJFrame(String nickname) {
        this.receiver = nickname;
        jLabel1.setText("Chat con " + receiver);
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 102, 255));

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("Scrivi");
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

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jButton2.setText("Manda un file");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton2MouseReleased(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Microsoft New Tai Lue", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addComponent(jButton2))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
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
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed
    
    /**
     * Metodo che gestisce la pressione del bottone "Scrivi"
     * @param evt Evento da gestire
     */
    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        
        String message = jTextField1.getText(); //messaggio da scrivere all'amico
        JSONObject msgObj = null;
        
        //invio del messaggio all'amico
        try {
           msgObj = new JSONObject(); 
           msgObj.put("sender", LoggedUserFrame.username);
           msgObj.put("receiver", receiver);
           msgObj.put("body", message);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        
        //spawn del thread che si occupa di mandare il messaggio al server
        ClientWriter msgWriter;
        try {
            msgWriter = new ClientWriter(msgObj.toString(), this);
        }
        catch(NullPointerException e) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile inviare il messaggio. Riprova");
            popUpMessage.setVisible(true);
            return;
        }
        Thread writerThread = new Thread(msgWriter);
        writerThread.start();
        
        //append del messaggio al log della chat
        String myChatUsername = "<" + LoggedUserFrame.username + ">";
        updateChatLog(myChatUsername + ": " + message);
        
        //reset del campo dove l'utente scriverà un altro messaggio
        jTextField1.setText("");
    }//GEN-LAST:event_jButton1MouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
    
    /**
     * Metodo che gestisce la pressione del bottone "Manda un file"
     * @param evt Evento da gestire
     */
    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased
        
        JSONObject sendFileRequest = new JSONObject();
        File file = null;
        
        /*
        apertura di un JFileChooser per permettere all'utente di
        navigare nel proprio file system e scegliere il file da inviare
        */
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
        
        /*
        invio della richiesta di mettersi in comunicazione con
        il destinatario al server
        */
        if(file.isFile()) {
            long fileSize = file.length();
            try {
                sendFileRequest.put("username", LoggedUserFrame.username); //nome del sender
                sendFileRequest.put("OP", RequestType.FILE2FRIEND); //operazione
                sendFileRequest.put("receiver", receiver); //utente destinatario
                sendFileRequest.put("fileName", file.getAbsolutePath()); //nome del file
                sendFileRequest.put("fileSize", fileSize); //dimensione del file (in byte)
                ClientMainJFrame.writeRequest(sendFileRequest.toString());
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            
            //lettura della risposta del server
            String reply = ClientMainJFrame.readReply();
            int filePort = 0; //porta del destinatario sulla quale il client manderà il file
            InetAddress ipAddr = null; //indirizzo IP del destinatario
            try {
                JSONObject replyObj = new JSONObject(reply);
                String replyCode = replyObj.getString("replycode");

                //se l'amico è passato a offline chiudo il frame
                if(replyCode.equals(ReplyType.FRIENDOFFLINE.name())) this.dispose();

                //se la richiesta è andata a buon fine leggo gli ulteriori dati dal server
                else if(replyObj.getString("replycode").equals("OK")) {
                    reply = ClientMainJFrame.readReply();
                    replyObj = new JSONObject(reply);
                    ipAddr = InetAddress.getByName(replyObj.getString("ipAddr"));
                    filePort = replyObj.getInt("filePort");
                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            catch(UnknownHostException e) {
                e.printStackTrace();
            }

            //apertura del SocketChannel su cui inviare il file
            InetSocketAddress iAddr = new InetSocketAddress(ipAddr,filePort);

            try( SocketChannel fileSocket = SocketChannel.open();
                 FileChannel fileReceiver = FileChannel.open(Paths.get(file.getAbsolutePath()), StandardOpenOption.READ);) {
                fileSocket.connect(iAddr);

                /*
                lettura del file dal FileChannel e invio del file al destinatario
                mediante il SocketChannel
                */
                int read = 0;
                ByteBuffer buf = ByteBuffer.allocateDirect(1024);
                while(read < fileSize) {
                  int tempRead = fileReceiver.read(buf);
                  read = read + tempRead;
                  byte[] bytes = new byte[tempRead];
                  buf.flip();
                  fileSocket.write(buf);
                  buf.clear();
                }
            }
            catch(IOException e) { //problema di I/O sul socket o sul FileChannel, terminazione del client
                LogoutTask logOut = new LogoutTask();
                Thread logOutThread = new Thread(logOut);
                logOutThread.start();
            }
        }
    }//GEN-LAST:event_jButton2MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private static javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JTextArea jTextArea2;
    private static javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Metodo che appende un messaggio al chat log
     * @param message Messaggio da appendere
     */
    public void updateChatLog(String message) {
        jTextArea2.append(message);
        jTextArea2.append("\n");
    }

}
