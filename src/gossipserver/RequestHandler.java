package gossipserver;

import java.io.*;
import java.net.*;
import org.json.*;
import java.util.*;
import java.rmi.RemoteException;

/**
 * Classe che implementa il task di gestione di una richiesta ricevuta dal client
 * @author Giulia Fois, Laura Bussi
 */
public class RequestHandler extends Handler implements Runnable {

    private Socket socket;
    private String requestString;
    
    /**
     * Costruttore della clase
     * @param request Una stringa contenente la descrizione della richiesta da gestire
     * @param sock Il socket da cui proviene la richiesta
     * @throws NullPointerException Se request == null o sock == null
     */
    public RequestHandler(String request, Socket sock) throws NullPointerException {
        super();
        if(request == null || sock == null) throw new NullPointerException();
        this.requestString = request;
        this.socket = sock;
    }
    
    /**
     * Implementazione dal task
     */
    @Override
    public void run() {
        JSONObject jsonReq = null; //JSONObject che conterrà la richiesta
        JSONObject replyObj = null; //JSONObject che conterrà la risposta
        //RequestImpl request = null; //da eliminare
        //Reply reply = null; // da eliminare
        int ret = 0; //valore di ritorno delle operazioni svolte per soddisfare la richiesta
        //campi presenti in tutte le richieste
        String operation = null; //operazione richiesta
        String username = null; //username del richiedente
        String friend; //dopo
        String receiver = null; //per i receiver del file
        String chatName; //dopo
        BufferedWriter replyWriter = null;
        String req = null;
        Chatroom chatroom = null;
        try {
            replyWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(SocketException e) {
            closeRequestSocket();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        try {
            jsonReq = new JSONObject(requestString);
            //campi presenti in tutte le richieste
            operation = jsonReq.getString("OP");
            username = jsonReq.getString("username");
            
            replyObj = new JSONObject(); //DOPO
            switch(operation) {
                case "REGISTER": //richiesta di registrazione 
                    User user = null;
                    user = new User(username, socket.getInetAddress());
                    user.setPassword(jsonReq.getString("password"));
                    user.setLanguage(jsonReq.getString("language"));
                    if(GossipServer.registerUser(user.getUsername(), user) == -1) replyObj.put("replycode",ReplyType.USRALREADY);
                    else replyObj.put("replycode", ReplyType.OK);
                    break;
                case "LOGIN": //richiesta di login
                    ret = GossipServer.logIn(username,jsonReq.getString("password"));
                    if(ret == -1) replyObj.put("replycode", ReplyType.USRNOEXISTS); 
                    else if(ret == -2)  replyObj.put("replycode",ReplyType.ONLINEALREADY);
                    else if(ret == -3) replyObj.put("replycode",ReplyType.WRONGPWD); //frame al client
                    else replyObj.put("replycode",ReplyType.OK);
                    replyObj.put("msgIn", GossipServer.listenerPort);
                    replyObj.put("rmi", GossipServer.RMIPort);
                    break;
                case "LOGOUT": //richiesta di logout
                    GossipServer.logOut(username);
                    replyObj.put("replycode", ReplyType.OK);
                    break;
                case "LOOKUP": //richiesta di lookup
                    if (GossipServer.userLookup(jsonReq.getString("userToSearch")) == null)
                        replyObj.put("replycode", ReplyType.USRNOTFOUND);
                    else
			replyObj.put("replycode", ReplyType.USRFOUND);
                    break;
                case "FRIENDSHIP": //richiesta di nuova amicizia
                    ret = GossipServer.addFriendship(username, jsonReq.getString("friend"));
                    if(ret == -1) replyObj.put("replycode", ReplyType.FOREVERALONE);
                    else if(ret == -2) replyObj.put("replycode", ReplyType.ALREADYFRIENDS);
                    else replyObj.put("replycode", ReplyType.OK);
                    break;
                case "LISTFRIEND": //richiesta di lista amici
                    //se l'utente non ha amici
                    if (GossipServer.userTable.get(username).numberOfFriends() == 0) {
                        ret = -1;
			replyObj.put("replycode", ReplyType.NOFRIENDS);
                    } 
                    //se l'utente ha almeno un amico
                    else {
                        replyObj.put("replycode", ReplyType.OK);
                        user = GossipServer.userLookup(username);
                        /*
                        vettore contenente i nomi degli amici affiancati
                        dal loro stato
                        */
                        Vector<String> friends = user.getFriendsList();
                        int num = user.numberOfFriends();
                        for(String s: friends) {
                            User fr = GossipServer.userLookup(s);
                            int friendIndex = friends.indexOf(s);
                            if(fr.getStatus() == 1) 
                                friends.set(friendIndex, s + " ON");
                            else
                                friends.set(friendIndex, s + " OFF");
                        }
                        /*
                        inserisco nella risposta il numero degli amici
                        e la un JsonArray contenente la lista degli amici
                        */ 
			replyObj.put("numberOfFriends", num);
                            if (num > 0) {
				replyObj.put("friendlist", friends);
                            }
                        }
                    break;
                case "FILE2FRIEND": //richiesta di invio di un file
                    receiver = jsonReq.getString("receiver");
                    /*il client fa la richiesta avendo già la chat del receiver aperta
                      quindi il receiver esiste e sono necessariamente già amici
                    */
                    //in questo caso il client chiuderà la finestra della chat
                    if(GossipServer.userTable.get(receiver).getStatus() == 0) replyObj.put("replycode",ReplyType.FRIENDOFFLINE);
                    else replyObj.put("replycode",ReplyType.OK);
                    break;
               case "CREATE": //richiesta di creazione di una chatroom
                    chatName = jsonReq.getString("groupname");
                    if (GossipServer.chatroomLookup(chatName) != null) {
                        replyObj.put("replycode", ReplyType.CHATALREADY);
                    } 
                    else {
                        //creazione dell'indirizzo multicast per questa chatroom
                        String lastByte = Integer.toString(GossipServer.multicastLastByte);
                        String multicastAddr = GossipServer.generateMulticastAddress(lastByte);
                        
                        //incremento dell'ultimo byte per la prossima chatroom
                        GossipServer.incrementLastByte();
                        
                        //aggiunta della chatroom nel server
                        chatroom = new Chatroom(chatName, username, InetAddress.getByName(multicastAddr));
                        GossipServer.registerChatroom(chatName, chatroom);
                        GossipServer.userTable.get(username).addGroup(chatName);
                        replyObj.put("replycode", ReplyType.OK);
                    }
                    break;
                case "ADDME": //richiesta di registrazione ad una chatroom
                    
                    chatName = jsonReq.getString("groupname");
                    chatroom = GossipServer.chatroomLookup(chatName);
                   
                    user = GossipServer.userLookup(username);
                    if (chatroom.isMember(username)) //se l'utente è già membro della chatroom
                        replyObj.put("replycode", ReplyType.USRALREADY);
                    else { //se l'utente non è membro della chatroom
                        
                        //aggiunta dell'utente alla chatroom
                        user.addGroup(chatName);
                        chatroom.addMember(username);
                        replyObj.put("replycode", ReplyType.OK);
                    }
                    break;
                case "CHATLIST": //richiesta della lista di chatroom
                    
                    replyObj.put("replycode", ReplyType.OK);
                    
                    //vettore dove saranno inserite tutte le chatroom
                    Vector<String> allChatrooms = new Vector<String>();
                    Collection<Chatroom> chatroomsCollection = GossipServer.chatroomTable.values();
                    
                    /*
                    aggiunta di tutte le chatroom al vettore,
                    affiancate dal ruolo dell'utente in esse
                    */
                    for(Chatroom c: chatroomsCollection) {
                        if(c.isCreator(username))
                                allChatrooms.add(c.getName() + " (creator)");
                            else if(c.isMember(username)) 
                                allChatrooms.add(c.getName() + " (subscribed)");
                            else
                                allChatrooms.add(c.getName());
                    }
                    
                    /*
                    inserzione nella risposta del numero delle chatroom
                    e della lista
                    */
                    int num = allChatrooms.size();
                    replyObj.put("numberOfChats", num);
                    if (num > 0) {
                        replyObj.put("chatlist", allChatrooms);
                    }
                    break;
                case "CLOSECHAT": //richiesta di chiusura di una chatroom
                    chatName = jsonReq.getString("groupname");
                    chatroom = GossipServer.chatroomLookup(chatName);
                    Vector<String> members = chatroom.getMembers(); //lista dei membri della chatroom
                    
                    //rimozione della chatroom
                    GossipServer.removeChatroom(chatroom, username);
                    
                    //callback di notifica della chiusura della chatroom (mandata agli utenti online)
                    for(String m: members) {
                        if(GossipServer.userTable.get(m).getStatus() == 1) {
                            ServerRemoteImpl.chatroomClosed(m, chatName);
                        }
                    }
                    
                    //rimozione della chatroom dalle liste delle chatroom di ogni utente
                    chatroom.removeAllUsers(username);
                    GossipServer.userTable.get(username).removeGroup(chatName);
                    replyObj.put("replycode", ReplyType.OK);
                    break;
                default: //risposta di default
                    replyObj.put("replycode", ReplyType.FAIL);
            }
        }
        catch(NullPointerException | JSONException | UnknownHostException | RemoteException e) {
            closeRequestSocket();
        }
        try {
            
            //scrittura della risposta
            replyWriter.write(replyObj.toString());
            replyWriter.newLine();
            replyWriter.flush();
            
            //operazioni aggiuntive
            if(operation.equals("LOGIN") && ret == 0) {
                
                /*
                apertura del socket per i messaggi in uscita verso il client
                e invio del numero di porta per i messaggi in entrata
                */
                int clientMessagePort = jsonReq.getInt("msgIn");
                Socket clientMessageWriter = new Socket(InetAddress.getByName("127.0.0.1"), clientMessagePort);
                GossipServer.userTable.get(username).setSocket(clientMessageWriter);
                
                //callback RMI del cambio di status a tutti gli amici dell'utente
                ServerRemoteImpl.onlineFriend(username);
            }
            else if(operation.equals("LOGOUT")) {
                //callback RMI del cambio di status a tutti gli amici dell'utente
                ServerRemoteImpl.offlineFriend(jsonReq.getString("username"));
                
                //chiusura del socket messaggi dell'utente
                Socket usrMsgSock = GossipServer.userTable.get(username).getMessageSocket();
                if(!usrMsgSock.isClosed() && usrMsgSock != null) {
                    usrMsgSock.close();
                }
            }
            else if(operation.equals("FRIENDSHIP") && ret == 0) {
                
                //callback RMI al nuovo amico dell'utente
                ServerRemoteImpl.newFriendship(jsonReq.getString("friend"), username);
            }
            
            if ((operation.equals("CREATE") || operation.equals("ADDME")) && ret == 0) {
		
                //invio all'utente l'indirizzo multicast del gruppo
                InetAddress addr = chatroom.getMulticastAddress();
                JSONObject chAddr = new JSONObject();
                chAddr.put("address", addr.toString());
                replyWriter.write(chAddr.toString());
                replyWriter.newLine();
                replyWriter.flush();
            }
            else if(operation.equals("FILE2FRIEND") && ret == 0) {
                /*
                manda al destinatario la richiesta di aprire un socket file
                nonchè il nome del file che riceverà e la sua dimensiome
                */
                String receiverIPAddr = GossipServer.userTable.get(receiver).getIp().getHostAddress();
                Socket receiverMessageSocket = GossipServer.userTable.get(receiver).getMessageSocket();
                BufferedWriter messageWriter = new BufferedWriter(new OutputStreamWriter(receiverMessageSocket.getOutputStream()));
                BufferedReader messageReader = new BufferedReader(new InputStreamReader(receiverMessageSocket.getInputStream()));
                int fileSize = jsonReq.getInt("fileSize");
                String fileName = jsonReq.getString("fileName");
                JSONObject fileRequest = new JSONObject();
                fileRequest.put("OP", "FILE");
                fileRequest.put("sender", username);
                fileRequest.put("fileSize", fileSize);
                fileRequest.put("fileName", fileName);
                String fileData = null;
                try {
                    messageWriter.write(fileRequest.toString());
                    messageWriter.newLine();
                    messageWriter.flush();
                    /*
                    riceve la risposta contenente la porta
                    e la inoltra al mittente
                    */
                    fileData = messageReader.readLine();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
                JSONObject fileDataObj = new JSONObject(fileData);
                fileDataObj.put("ipAddr", receiverIPAddr);
                replyWriter.write(fileDataObj.toString());
                replyWriter.newLine();
                replyWriter.flush();
             }
        }
        catch(SocketException e) {
            if(socket != null) {
                try {
                    socket.close();  
                }
                catch(IOException ex) {
                    e.printStackTrace();
                }
            }
        }
        catch(JSONException | IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Metodo di chiusura del socket richieste, invocato
     * in caso di errori su esso
     */
    public void closeRequestSocket() {
        try {
            socket.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
