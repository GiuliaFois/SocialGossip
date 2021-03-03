package gossipserver;
import java.rmi.*;

import gossipclient.ClientRemoteInterface;

/**
 * Interfaccia contenente la segnatura dei metodi di registrazione 
 * e deregistrazione al servizio RMI offerti al client
 * @author Giulia Fois, Laura Bussi
 */
public interface ServerRemoteInterface extends Remote {
    
    /**
     * Metodo che implementa la registrazione ad un servizio RMI 
     * da parte di un client
     * @param usrname Username dell'utente che si registra
     * @param clientInt Interfaccia remota dell'utente
     * @throws RemoteException 
     */
    public void register(String usrname, ClientRemoteInterface clientInt) throws RemoteException;
    
    /**
     * Metodo che implementa la deregistrazione ad un servizio RMI 
     * da parte di un client
     * @param usrname Username dell'utente che si registra
     * @param clientInt Interfaccia remota dell'utente
     * @throws RemoteException 
     */
    public void unregister(String usrname, ClientRemoteInterface clientInt) throws RemoteException;
    
}
