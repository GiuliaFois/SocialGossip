package gossipclient;

import java.rmi.*;
import java.rmi.server.*;

/**
 * Classe che implementa l'interfaccia remota del client
 * ClientRemoteInterface per le callbacks
 * @author Giulia Fois, Laura Bussi
 */
public class ClientRemoteImpl extends RemoteObject implements ClientRemoteInterface {
    
    /**
     * Costruttore della classe
     * @throws RemoteException 
     */
    public ClientRemoteImpl() throws RemoteException {}
    
    /**
     * Implementazione del metodo contenuto in ClientRemoteInterface
     * @param nick Username dell'utente che ha appena aggiunto questo utente agli amici
     */
    public void notifyNewFriend(String nick) {
	String notifyMsg = new String("L'utente " + nick + " ti ha aggiunto come amico");
	LoggedUserFrame.updateLog(notifyMsg);
    }
    
    /**
     * Implementazione del metodo contenuto in ClientRemoteInterface
     * @param nick Username dell'utente che ha appena cambiato status
     * @param stat Status dell'utente: 0 se è passato a offline, 1 se è passato a online
     */
    public void notifyChangeStatus(String nick, int stat) {
        
        String status;
        if(stat == 0)
                status = "offline";
        else
                status = "online";
        String notifyMsg = new String("L'utente " + nick + " è ora " + status);
        LoggedUserFrame.updateLog(notifyMsg);
    }
        
    /**
    * Implementazione del metodo contenuto in ClientRemoteInterface
    * @param chatname Nome della chatroom che è stata appena chiusa
    */
    public void notifyChatClosing(String chatname) {
        
        System.out.println("SONO IN NOTIFYCHATCLOSING");
        
        String notifyMsg = "La chatroom " + chatname + " è stata chiusa";
        LoggedUserFrame.updateLog(notifyMsg);
        
        //se il frame della chatroom è attivo lo chiudo
        if(LoggedUserFrame.containsChatroomFrame(chatname)) {
            ChatroomJFrame frame = LoggedUserFrame.getChatroomFrame(chatname);
            frame.setVisible(false);
        }
    }
}
