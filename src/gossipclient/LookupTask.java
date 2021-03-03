package gossipclient;

import org.json.*;
import java.net.*;

/**
 * Classe che implementa il task di ricerca di un utente
 * @author Giulia Fois, Laura Bussi
 */
public class LookupTask implements Runnable {
    
    String userToSearch; //username dell'utente da cercare
    String username; //username dell'utente che manda la richiesta
    
    /**
     * Metodo costruttore
     * @param userToSearch Username dell'utente da cercare
     * @param username Username dell'utente che manda la richiesta
     */
    public LookupTask(String userToSearch, String username) {
        this.userToSearch = userToSearch;
        this.username = username;
    }
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() {
        
        String replycode = null;
        
        try {
          //invio della richiesta di lookup
          JSONObject request = new JSONObject();
          request.put("OP",RequestType.LOOKUP);
          request.put("username", username);
          ClientMainJFrame.writeRequest(request.toString());
           
          //lettura della risposta
          String reply = ClientMainJFrame.readReply();
          JSONObject replyObj = new JSONObject(reply);
          replycode = replyObj.getString("replycode");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        
        //se l'utente non è stato trovato
        if(replycode.equals(ReplyType.USRNOTFOUND.name())) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Utente non trovato");
            popUpMessage.setVisible(true);
            return;
        }
        
        //se l'utente è stato trovato
        else {
            //attivazione del frame per l'utente trovato
            UserFoundFrame usrFound = new UserFoundFrame(userToSearch);
            usrFound.setJlabelText(userToSearch);  
            usrFound.setVisible(true);
        }
    }
}
