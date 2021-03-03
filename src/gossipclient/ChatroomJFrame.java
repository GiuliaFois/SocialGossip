package gossipclient;

import java.net.*;
import org.json.*;
import java.io.IOException;

/**
 * La classe implementa il task che si occupa di gestire la chatroom su cui
 * l'utente vuole scrivere e ricevere messaggi
 * @author Giulia Fois, Laura Bussi
 */
public class ChatroomJFrame extends javax.swing.JFrame {

    String groupName; //nome della chatroom
    private DatagramPacket packet;
    
    /**
     * Metodo costruttore della classe
     * @param groupName Il nome della chatroom
     */
    public ChatroomJFrame(String groupName) {
        initComponents();
        this.groupName = groupName;
        jLabel1.setText("Chat con il gruppo: " + this.groupName);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 102, 255));

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

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

        jLabel1.setFont(new java.awt.Font("Microsoft New Tai Lue", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                            .addComponent(jTextField1))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
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

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * Metodo che gestisce la pressione del bottone "Scrivi", ovvero 
     * l'invio di un messaggio alla chatroom
     * @param evt Evento da gestire
     */
    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        String message = jTextField1.getText();
        JSONObject msgObj = null;
        
        /**
         * Easter egg da Wolfenstein 3D: chiusura speciale della chatroom
         */
        if(message.equals("To big bad wolf: eliminate " + groupName)) {
            msgObj = new JSONObject();
            try {
                //composizione e invio della richiesta di chiusura
                msgObj.put("OP", RequestType.CLOSECHAT);
                msgObj.put("username", LoggedUserFrame.username);
                msgObj.put("groupname", groupName);
                String request = msgObj.toString();
                ClientMainJFrame.writeRequest(request);
                String reply = ClientMainJFrame.readReply();
                
                //lettura della risposta
                JSONObject replyObj;
                replyObj = new JSONObject(reply);
                /*
                caso in cui ci sia stato qualche problema con la chiusura
                */
                if(!replyObj.getString("replycode").equals(ReplyType.OK.name())) {
                    PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile chiudere la chatroom");
                    popUpMessage.setVisible(true);
                } 
                else {  //la chiusura è andata a buon fine
                   PopUpJFrame popUpMessage = new PopUpJFrame("Hai scovato l'Easter Egg! Chatroom chiusa");
                }
            } catch (JSONException e) {
                 PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile chiudere la chatroom");
                 popUpMessage.setVisible(true);
            }
        } 
        else if(!message.equals("")) { //non si può inviare un messaggio vuoto
            PopUpJFrame popUpErrorMessage = new PopUpJFrame("Impossibile inviare il messaggio. Riprova");
            try {
                //composizione del messaggio da mandare al server
                msgObj = new JSONObject();
                msgObj.put("sender", LoggedUserFrame.username);
                msgObj.put("groupname", groupName);
                msgObj.put("body", message);
            }
            catch(JSONException e) {
                popUpErrorMessage.setVisible(true); 
            }
            try(DatagramSocket socket = new DatagramSocket();) {
                //invio del messaggio al server
                byte[] toSend = new byte[2048];
                toSend = msgObj.toString().getBytes();
                packet = new DatagramPacket(toSend,toSend.length,InetAddress.getLoopbackAddress(),5000);
                socket.send(packet);
            } 
            catch(SocketException e) {
                popUpErrorMessage.setVisible(true);
                return;
            } 
            catch(IOException e) {
                popUpErrorMessage.setVisible(true);
                return;
            }
            
            //messaggio inviato correttamente: append sul chatroom log dell'utente
            String myChatUsername = "<" + LoggedUserFrame.username + ">";
            updateChatLog(myChatUsername + ": " + message);
            jTextField1.setText("");
        }
    }//GEN-LAST:event_jButton1MouseReleased
    
    /**
     * Metodo che appende un messaggio al chatroom log
     * @param message Messaggio da appendere al chatroom log
     */
    public static void updateChatLog(String message) {
    jTextArea2.append(message);
    jTextArea2.append("\n");
    }
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private static javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JTextArea jTextArea2;
    private static javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
