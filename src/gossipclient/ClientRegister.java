package gossipclient;

import org.json.*;
import java.util.*;

/**
 * Classe che implementa il task di invio richiesta 
 * di registrazione
 * @author Giulia Fois, Laura Bussi
 */
public class ClientRegister implements Runnable {
    
    String username; //username dell'utente
    String pwd; //password dell'utente
    String lang; //linguaggio scelto dall'utente
    /*
    hashmap delle lingue: associa alla stringa contenente il nome della lingua
    il relativo codice ISO 639-2
    */
    HashMap<String,String> languagesTable;
    
    /**
     * Metodo costruttore
     * @param username Username dell'utente
     * @param pwd Password dell'utente
     * @param language Lingua dell'utente
     * @throws NullPointerException se username == null o pwd == null o language == null
     */
    public ClientRegister(String username, String pwd, String language) {
        if(username == null || pwd == null || language == null) throw new NullPointerException();
        this.username = username;
        this.pwd = pwd;
        this.lang = language;
        //inizializzazione della tabella hash con le lingue disponibili
        languagesTable = new HashMap<String,String>();
        languagesTable.put("Italian", "it");
        languagesTable.put("English", "en");
        languagesTable.put("German", "de");
        languagesTable.put("French", "fr");
        languagesTable.put("Russian", "ru");
        languagesTable.put("Spanish", "es");
        languagesTable.put("Japanese", "ja");
        languagesTable.put("Chinese", "zh");
    }
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() {
        JSONObject req = new JSONObject();
        String reply;
        JSONObject replyObj;
        String replyCode = null;
        
        //conversione della lingua nel suo codice ISO
        String language = languagesTable.get(lang);
        
        //invio della richiesta
        try {
            req.put("OP", RequestType.REGISTER);
            req.put("username", username);
            req.put("password", pwd);
            req.put("language", language);
            ClientMainJFrame.writeRequest(req.toString());
            
            //lettura della risposta
            reply = ClientMainJFrame.readReply();
            replyObj = new JSONObject(reply);
            replyCode = replyObj.getString("replycode");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        
        //caso in cui l'utente sia già registrato
        if(replyCode.equals(ReplyType.USRALREADY.name())) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Utente già registrato. Riprova");
            popUpMessage.setVisible(true);
            return;
        }
     
        //caso in cui la registrazione sia andata a buon fine
        else {
            PopUpJFrame popUpMessage = new PopUpJFrame("Registrazione avvenuta con successo!");
            popUpMessage.setVisible(true);
        }
    }
}