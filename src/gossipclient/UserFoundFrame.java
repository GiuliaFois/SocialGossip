package gossipclient;

import org.json.*;

/**
 * Classe che implementa il frame che si attiva
 * nel momento in cui un utente cercato viene trovato,
 * tramite cui si può inviare anche una richiesta di
 * amicizia 
 * @author giuli
 */
public class UserFoundFrame extends javax.swing.JFrame {
 
    String user; //utente trovato
    
    public UserFoundFrame(String user) {
        initComponents();
        this.user = user;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 102, 255));

        jLabel1.setFont(new java.awt.Font("Microsoft New Tai Lue", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));

        jButton1.setText("AGGIUNGI AMICO");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Metodo che gestisce la pressione sul bottone "Aggiungi Amico", tramite
     * cui inviare la richiesta di amicizia al server
     * @param evt Evento da gestire
     */
    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased

        String replycode = null;
        
        try {
            
           //invio della richiesta di amicizia
           JSONObject req = new JSONObject();
           req.put("username", LoggedUserFrame.username);
           req.put("OP", RequestType.FRIENDSHIP);
           req.put("friend", user);
           ClientMainJFrame.writeRequest(req.toString());
           
           //ricezione della risposta
           String reply = ClientMainJFrame.readReply();
           JSONObject replyObj = new JSONObject(reply);
           replycode = replyObj.getString("replycode");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        
        //se l'utente era già amico 
        if(replycode.equals(ReplyType.ALREADYFRIENDS.name())) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Tu e " + user + " siete già amici");
            popUpMessage.setVisible(true);
        }
        
        else if(replycode.equals(ReplyType.FOREVERALONE.name())) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Non puoi inviarti la richiesta di amicizia da solo");
            popUpMessage.setVisible(true);
        }
        
        //se la richiesta è andata a buon fine
        else {
           PopUpJFrame popUpMessage = new PopUpJFrame("Tu e " + user + " siete ora amici!");
           popUpMessage.setVisible(true);
        }    
    }//GEN-LAST:event_jButton1MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Metodo che aggiunge del testo all'etichetta
     * del frame
     * @param s 
     */
    public void setJlabelText(String s) {
        this.jLabel1.setText(s);
    }
}

