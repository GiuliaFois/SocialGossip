package gossipclient;

import java.rmi.*;

/**
 * Interfaccia disponibile al server per le callbacks di notifica
 * @author giuli
 */
public interface ClientRemoteInterface extends Remote {
    
    /**
    * Metodo callback di notifica di nuova amicizia
    * @param nick Username dell'utente che ha appena aggiunto questo utente agli amici
    */
    public void notifyNewFriend(String nick) throws RemoteException;

    /**
    * Metodo callback di notifica di cambio di stato di un utente
    * @param nick Username dell'utente che ha appena cambiato status
    * @param stat Status dell'utente: 0 se è passato a offline, 1 se è passato a online
    */
    public void notifyChangeStatus(String nick, int stat) throws RemoteException;
    
    /**
     * Metodo callback di notifica di chiusura di una chatroom
     * @param chatname Nome della chatroom che è stata appena chiusa
     * @throws RemoteException 
     */
    public void notifyChatClosing(String chatname) throws RemoteException;
}
