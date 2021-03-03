package gossipserver;

import gossipclient.ClientRemoteInterface;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * La classe User contiene campi e metodi relativi alla gestione del singolo utente
 * @author Giulia Fois, Laura Bussi
 */
public class User {
    private String username; //username dell'utente
    private String password; //password dell'utente
    private CopyOnWriteArrayList<String> friends; //lista degli amici dell'utente
    private CopyOnWriteArrayList<String> chatroomList;
    private String language; //lingua dell'utente
    private int status; //stato dell'utente (online o offline)
    private int messagePort; //porta del socket su cui mandare messaggi all'utente (serve??)
    private InetAddress ipAddr; //indirizzo IP dell'utente
    private int datagramPort; //??
    private int multicastPort; //??
    private Socket messageSocket; //socket su cui mandare messaggi all'utente 
    private ClientRemoteInterface remoteInterface; //interfaccia RMI dell'utente
    
    /**
     * Costruttore della classe
     * @param usrname Username dell'utente
     * @param ipAddr Indirizzo IP dell'utente
     * @throws NullPointerException Se usrname == null o ipAddr == null
     */
    public User(String usrname, InetAddress ipAddr) throws NullPointerException {
        if(usrname == null || ipAddr == null) throw new NullPointerException();
        this.username = usrname;
        this.friends = new CopyOnWriteArrayList<String>();
        this.chatroomList = new CopyOnWriteArrayList<String>();
        this.ipAddr = ipAddr;
        this.remoteInterface = null;
    }
    
    /**
     * Restituisce l'username dell'utente
     * @return L'username dell'utente
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
    * Ritorna la password dell'utente
    * @return La password dell'utente
    */
    public String getPassword() {
        return this.password;
    }
    
    /**
    * Assegna una password all'utente
    * @param pwd La password da assegnare
    */
    public void setPassword(String pwd) throws NullPointerException, IllegalArgumentException {
        if(pwd == null) throw new NullPointerException();
        if(pwd.equals("")) throw new IllegalArgumentException();
        this.password = pwd;
    }
    
    /**
    * Ritorna lo stato online/offline dell'utente
    * @return 1 se l'utente è online, 0 se è offline
    */
    public int getStatus() {
        return this.status;
    }
    
    /**
    Cambia lo stato dell'utente
    * @param status Un intero che rappresenta il nuovo stato da assegnare all'utente
    */
    public void setStatus(int status) {
        this.status = status;
    }
    
    /**
    * Ritorna la lingua dell'utente
    * @return Una stringa contenente la lingua dell'utente
    */
    public String getLanguage() {
        return this.language;
    }
    
    /**
    * Assegna una lingua all'utente
    * @param lang La lingua da assegnare all'utente
    * @throws NullPointerException se lang == null
    */
    public void setLanguage(String lang) throws NullPointerException {
        if(lang == null) throw new NullPointerException();
        this.language = lang;
    }
    
    /**
    * Ritorna il numero di amici dell'utente
    * @return Un intero che corrisponde alla dimensione del vettore amici dell'utente
    */
    public int numberOfFriends() {
        return this.friends.size();
    }
    
    /**
    * Aggiunge un amico alla lista amici dell'utente
    * @param friend L'amico da aggiungere alla lista amici dell'utente
    * @throws NullPointerException Se friend == null
    */
    public void addFriend(String friend) throws NullPointerException {
        if(friend == null) throw new NullPointerException();
        this.friends.add(friend); //bello java <3
    }
    
    /**
     * Verifica se un utente sia nella lista amici di questo utente
     * @param friend L'amico che si sta cercando nella lista
     * @return true se friend è amico dell'utente
     *         false altrimenti
     * @throws NullPointerException Se friend == null
     */
    public boolean friendWith(String friend) throws NullPointerException {
        if(friend == null) throw new NullPointerException();
        if(this.friends.contains(friend)) return true;
        else return false;
    }
    
    /**
     * Ritorna la lista degli amici dell'utente
     * @return Un vector contenente gli amici dell'utente
     */
    public Vector<String> getFriendsList() {
        return new Vector<String>(friends);
    }
    
    /**
     * Ritorna l'indirizzo IP dell'utente
     * @return L'indirizzo IP del client
     */
    public InetAddress getIp() {
        return this.ipAddr;
    }
    
    /**
     * Ritorna la porta del socket messaggi dell'utente
     * @return Un intero contenente la porta messaggi dell'utente
     */
    public int getMessagePort() {
        return this.messagePort;
    }
    
    /**
     * Assegna una porta per il socket messaggi dell'utente
     * @param msgPort La porta da assegnare 
     * @throws IllegalArgumentException Se msgPort è una porta nota 
     */
    public void setMessagePort(int msgPort) throws IllegalArgumentException {
        if(msgPort < 1025) throw new IllegalArgumentException();
        this.messagePort = msgPort;
    }
    
    /**
     * Ritorna il socket messaggi dell'utente
     * @return L'oggetto Socket corrispondente alla connessione messaggi dell'utente
     */
    public Socket getMessageSocket() {
        return this.messageSocket;
    }
    
    /**
     * Assegna il socket messaggi all'utente
     * @param s L'oggetto Socket corrispondente alla connessione messaggi dell'utente
     * @throws NullPointerException Se s == null
     */
    public void setSocket(Socket s) throws NullPointerException {
        if(s == null) throw new NullPointerException();
        this.messageSocket = s;
    }

    /**
     * Ritorna l'interfaccia remota dell'utente
     * @return L'interfaccia remota dell'utente 
     */
    public ClientRemoteInterface getRemoteInterface() {
        return this.remoteInterface;
    }

    /**
     * Assegna un'interfaccia remota all'utente
     * @param remoteInterface L'interfaccia RMI da assegnare all'utente
     * @throws NullPointerException Se remoteInterface == null
     */
    public void setInterface(ClientRemoteInterface remoteInterface) throws NullPointerException {
        if(remoteInterface == null) throw new NullPointerException();
        this.remoteInterface = remoteInterface;
    }
    
    /**
     * Rimuove l'interfaccia RMI all'utente
     */
    public void unsetInterface() {
        this.remoteInterface = null;
    }
    
    /**
     * Aggiunge una chatroom alla lista delle chatroom di un utente
     * @param chatName Il nome della chatroom da aggiungere
     */
    public void addGroup(String chatName) {
        this.chatroomList.add(chatName);
    }
     
    /**
     * Rimuove una chatroom dalla lista delle chatroom di un utente
     * @param chatName Il nome della chatroom da rimuovere
     */
    public void removeGroup(String chatName) {
        this.chatroomList.remove(chatName);
    }
    
    /**
     * Ritorna il numero di chatroom a cui è iscritto l'utente
     * @return 
     */
    public int numberOfChats() {
        return this.chatroomList.size();
    }
     
    /**
     * Ritorna la lista delle chatroom a cui è iscritto l'utente
     * @return Un vector contenente la lista di chatroom dell'utente
     */
    public Vector<String> getChatsList() {
        return new Vector<String>(chatroomList);
    }
}