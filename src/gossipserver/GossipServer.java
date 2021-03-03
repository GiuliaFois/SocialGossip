package gossipserver;

import java.util.concurrent.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * La classe GossipServer implementa il main dell'applicazione lato server,
 * che si occupa di avviare i thread necessari all'attività vera e propria
 * del server
 *
 * @author Giulia Fois, Laura Bussi
 *
 */
public class GossipServer {
	
    public static ConcurrentHashMap<String,User> userTable; //hash table contenente gli utenti di SocialGossip
    //hash table contenente le chatroom aperte  dagli utenti in SocialGossip
    public static ConcurrentHashMap<String,Chatroom> chatroomTable;
    public static CopyOnWriteArrayList<String> onlineUsers; //lista degli utenti online in SocialGossip
    public static int requestPort = 1026; //porta del socket su cui il server riceve le richieste
    public static int listenerPort = 1028; //porta del socket su cui il server riceve i messaggi della chat utenti
    public static int messagePort;
    public static int udpPort = 5000; //porta utilizzata per il multicast
    public static int RMIPort = 6000; //porta utilizzata per il servizio RMI
    //stringa fissa per le richieste REST di traduzione 
    public static final String restReq = "https://api.mymemory.translated.net/get?q=";
    //interfaccia RMI del server
    public static ServerRemoteImpl remoteServer;
    //massima lunghezza dei nomi utente
    public static int MAX_NAME_LENGTH;
    public static String multicastFirstBytes = "225.255.255.";
    public static int multicastLastByte = 0; //per gli indirizzi multicast

    public static void main(String[] args) {
        System.out.println("Server started");

        //registrazione del servizio RMI
        try {
            remoteServer = new ServerRemoteImpl();
            ServerRemoteInterface stub = (ServerRemoteInterface) UnicastRemoteObject.exportObject(remoteServer, 40000);
            String remoteServerName = "RemoteServer";
            LocateRegistry.createRegistry(RMIPort);
            Registry registry = LocateRegistry.getRegistry(RMIPort);
            registry.bind(remoteServerName, stub);
        }
        catch(RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

        //costruzione delle strutture dati 
        userTable = new ConcurrentHashMap<String,User>(1024);
        chatroomTable = new ConcurrentHashMap<String,Chatroom>(512);

        //avvio dei servizi del server
        RequestListener requestListener = new RequestListener();
        Thread requestThread = new Thread(requestListener);
        MessageListener messageListener = new MessageListener();
        Thread messageListenerThread = new Thread(messageListener);
        ChatroomMessageListener chatroomListener = new ChatroomMessageListener();
        Thread chatroomThread = new Thread(chatroomListener);
        
        requestThread.start();
        messageListenerThread.start();
        chatroomThread.start();
    }

    /**
     * @overview Aggiunge un nuovo utente alla tabella hash degli utenti, se non è già registrato
     * @param usrname Username del nuovo utente
     * @param usrObj  Oggetto relativo all'utente da aggiungere alla tabella
     * @return 0 in caso di successo
     *         -1 se l'utente è già registrato
     * @throws NullPointerException Se usrname == null o usrObj == null
     */
    public static int registerUser(String usrname, User usrObj) throws NullPointerException {
        if(usrname == null || usrObj == null) throw new NullPointerException(); //lancio eccezioni -> catturo e chiudo tutto con un metodo
        if(userLookup(usrname) != null) return -1;
        userTable.put(usrname, usrObj);
        return 0;
    }

    /**
     * @overview Implementa il login di un utente
     * @param usrname Username dell'utente che fa il login
     * @param password Password dell'utente che fa il login
     * @return 0 se il login va a buon fine
     *         -1 se l'utente non è registrato
     *         -2 se l'utente è già online
     *         -3 se la password fornita dall'utente non è corretta
     * @throws NullPointerException Se usrname == null o password == null
     */
    public static int logIn(String usrname, String password) throws NullPointerException {
            if(usrname == null || password == null) throw new NullPointerException();
            if(userLookup(usrname) == null) return -1;
            if(userTable.get(usrname).getStatus() == 1) return -2;
            if(!password.equals(userTable.get(usrname).getPassword())) return -3;
            userTable.get(usrname).setStatus(1);
            return 0;
    }

    /**
     * @overview Implementa il logout di un utente
     * @param usrname Username dell'utente che deve fare il logout
     * @return 0 (l'operazione non può mai fallire) (posso metter void come return type)
     * @throws NullPointerException Se usrname == null
     */
    public static void logOut(String usrname) throws NullPointerException {
            if(usrname == null) throw new NullPointerException();
            userTable.get(usrname).setStatus(0);
            //tolgo dalla lista online??
    }

    /**
     * @overview mplementa la ricerca di un utente
     * @param usrname L'username dell'utente da cercare
     * @return L'oggetto contenente l'utente se questo è registrato, null altrimenti
     * @throws NullPointerException Se usrname == null
     */
    public static User userLookup(String usrname) throws NullPointerException {
        if(usrname == null) throw new NullPointerException();
        if(userTable.containsKey(usrname)) return userTable.get(usrname);
        else return null;
    }
    
    /**
     * @overview Implementa l'aggiunta dell'amicizia tra due utenti
     * @param usrname L'utente che richiede l'amicizia
     * @param newFriend L'utente da aggiungere tra gli amici
     * @return 0 se l'operazione ha successo
     *         -1 se l'utente chiede l'amicizia a se stesso
     *         -2 se i due utenti sono già amici
     * @throws NullPointerException Se usrname == null o newFriend == null
     */
    public static int addFriendship(String usrname, String newFriend) throws NullPointerException {
        if(usrname == null || newFriend == null) throw new NullPointerException();
        if(usrname.equals(newFriend)) return -1;
        User usrObj = userTable.get(usrname);
        if(usrObj.friendWith(newFriend)) return -2;
        usrObj.addFriend(newFriend);
        return 0;
    }
    
     /**
     * @overview Implementa la ricerca di una chatroom
     * @param chatName Nome della chatroom da cercare
     * @return L'oggetto contenente la chatroom se esiste, null altrimenti
     * @throws NullPointerException Se chatName == null
     */
     public static Chatroom chatroomLookup(String chatName) throws NullPointerException {
        if(chatName == null) throw new NullPointerException();
        if(chatroomTable.containsKey(chatName)) return chatroomTable.get(chatName);
        return null;
    }
    
    
    /**
	* @overview Metodo di supporto per la generazione degli indirizzi di multicast 
	* incrementa l'ultimo byte dell'ultimo indirizzo generato 
	*/
    public static synchronized void incrementLastByte() {
        multicastLastByte ++;
    }
    
    /**
	* @overview Metodo per generare multicast addresses 
	* concatena il campo multicastFirstByte con l'ultimo byte generato
	* @return addr, l'indirizzo generato 
	*/
    public static synchronized String generateMulticastAddress(String lastByte) {
        if(lastByte == null) throw new NullPointerException();
        String addr = multicastFirstBytes + lastByte;
        return addr;
    }
   
	/**
	* @overview Metodo per registrare una nuova chatroom, se questa non è già presente nella chatroomTable
	* @param chName     il nome della chatroom da registrare
	* @param chRoom     l'oggetto Chatroom da inserire nella chatroomTable
	* @return 0 in caso di successo, -1 altrimenti
	*/
    public static int registerChatroom(String chName, Chatroom chRoom) {
        if(GossipServer.chatroomLookup(chName) != null) return -1;
  
        chatroomTable.put(chName,chRoom);
        System.out.println("chatroom " + chName + " inserita");
        return 0;
    }

	/**
	* @overview Medoto per la rimozione di una chatroom
	* @param chatroom   il nome della chatroom da rimuovere
	* @param username   il nome dell'utente richiedente
	* @return 0 in caso di successo, -1 altrimenti
	*/     
    public static int removeChatroom(Chatroom chatroom, String username) {
        if(!chatroom.isCreator(username))
            return -1;
        else {
            chatroomTable.remove(chatroom.getName());
            return 0;
        }
    }
}
