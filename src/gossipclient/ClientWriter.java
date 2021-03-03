package gossipclient;

import org.json.*;

/**
 * La classe si occupa di inviare messaggi al server
 * che li dirotterà all'utente designato come destinatario
 * @author Giulia Fois, Laura Bussi
 */
public class ClientWriter implements Runnable {
	
    String message; //messaggio da inviare
    FriendChatJFrame frame; //frame della chat con il destinatario

    /**
     * Metodo costruttore
     * @param message Messaggio da inviare al server
     * @param friendFrame Frame della chat
     * @throws NullPointerException se message == null
     */
    public ClientWriter(String message, FriendChatJFrame friendFrame) {
        if(message == null) throw new NullPointerException();
        this.message = message;
        this.frame = friendFrame;
    }

    /**
     * Implementazione del task
     */
    @Override
    public void run() {
	
        String replycode = null; //codice di risposta mandato dal server
        
        try {
            //scrittura del messaggio
            LoggedUserFrame.writeMessage(message);
            
            //lettura della risposta
            String reply = LoggedUserFrame.readMessageReply();
            JSONObject msgObj = new JSONObject(reply);
            replycode = msgObj.getString("replycode");
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
        
        //caso in cui l'amico sia passato a offline: chiudo il frame della chat amici
        if(replycode.equals(ReplyType.FRIENDOFFLINE.name())) {
            frame.dispose();
        }
    } 
}
	


