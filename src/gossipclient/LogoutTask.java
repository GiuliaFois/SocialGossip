package gossipclient;

import org.json.*;
import java.rmi.RemoteException;

/**
 * Classe che implementa il task di logout da Social Gossip
 * @author Giulia Fois, Laura Bussi
 */
public class LogoutTask implements Runnable {
    
    /**
     * Metodo costruttore
     */
    public LogoutTask(){};
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() { 
        //invio della richiesta di logout
        JSONObject logoutReq = new JSONObject();
        JSONObject logoutRep;
        try {
           logoutReq.put("OP", RequestType.LOGOUT);
           logoutReq.put("username", LoggedUserFrame.username);
           ClientMainJFrame.writeRequest(logoutReq.toString());
           
           //ricezione della risposta
           String reply = ClientMainJFrame.readReply();
           logoutRep = new JSONObject(reply);
           
           //se la richiesta di logout è andata a buon fine
           if(logoutRep.getString("replycode").equals(ReplyType.OK.name())) {
               //deregistrazione dal servizio RMI 
               LoggedUserFrame.server.unregister(LoggedUserFrame.username,LoggedUserFrame.stub);
            }
           
           System.exit(0);
        }
        catch(RemoteException | JSONException e) {
            e.printStackTrace();
        }
    }
}
