package gossipserver;

import gossipclient.ClientRemoteInterface;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.Collection;

/**
 * La classe implementa i metodi dell'interfaccia
 * remota del server
 * @author giuli
 */
public class ServerRemoteImpl extends RemoteServer implements ServerRemoteInterface {
	
        /**
         * Metodo costruttore
         */
	public ServerRemoteImpl() {
            super();
	}
	
	@Override
	public void register(String usrname, ClientRemoteInterface clientInt) throws RemoteException {
            User usr = GossipServer.userTable.get(usrname);
            if(usr.getRemoteInterface() == null) { //aggiunge l'interfaccia remota ai dati del client
                    usr.setInterface(clientInt);
            }
        }

	@Override
	public void unregister(String usrname, ClientRemoteInterface clientInt) throws RemoteException {
            User usr = GossipServer.userTable.get(usrname); 
            usr.unsetInterface(); //rimuove l'interfaccia remota dai dati del client
	}
	
        /**
         * Metodo che implementa la callback di avviso di cambiamento
         * di stato di un utente da offline ad online
         * @param onlineFriend L'utente che è diventato online
         * @throws RemoteException 
         */
	public static synchronized void onlineFriend(String onlineFriend) throws RemoteException { 
            //manda la callback a tutti coloro che hanno l'utente tra gli amici
            Collection<User> users = GossipServer.userTable.values();
            for(User u: users) {
                if(u.friendWith(onlineFriend) && u.getStatus() == 1) //manda la callback solo se l'utente è online
                    u.getRemoteInterface().notifyChangeStatus(onlineFriend, 1);
            }
	}
	
        /**
         * Metodo che implementa la callback di avviso di cambiamento
         * di stato di un utente da online a offline
         * @param offlineFriend L'utente che è passato offline
         * @throws RemoteException 
         */
	public static synchronized void offlineFriend(String offlineFriend) throws RemoteException {
            //manda la callback a tutti coloro che hanno l'utente tra gli amici
            Collection<User> users = GossipServer.userTable.values();
            for(User u: users) {
                if(u.friendWith(offlineFriend) && u.getStatus() == 1) //manda la callback solo se l'utente è online
                    u.getRemoteInterface().notifyChangeStatus(offlineFriend, 0);
            }
        }
	
        /**
         * Metodo che implementa la callback di notifica di
         * una nuova amicizia
         * @param usrToUpdate Utente a cui mandare la notifica
         * @param newFriend Utente che ha creato il legame di amicizia
         * @throws RemoteException 
         */
	public static synchronized void newFriendship(String usrToUpdate, String newFriend) throws RemoteException {
            //manda la callback al nuovo amico dell'utente
            User usr = GossipServer.userTable.get(usrToUpdate);
            if(GossipServer.userTable.get(usrToUpdate).getStatus() == 1) //manda la callback solo se l'utente è online
                usr.getRemoteInterface().notifyNewFriend(newFriend);
	}
        
        /**
         * Metodo che implementa la callback di notifica
         * della chiusura di una chatroom
         * @param usrToUpdate Utente a cui mandare la notifica
         * @param chatname Nome della chatroom chiusa
         * @throws RemoteException 
         */
        public static synchronized void chatroomClosed(String usrToUpdate, String chatname) throws RemoteException {
            /*
            manda la callback ad un utente della chatroom: nella classe
            RequestHandler, che gestisce la chiusura della chatroom,
            questa callback è invocata per tutti i membri di essa
            */
            User usr = GossipServer.userTable.get(usrToUpdate);
            usr.getRemoteInterface().notifyChatClosing(chatname);
        }
    }
