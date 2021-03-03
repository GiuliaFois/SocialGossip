package gossipclient;

import org.json.*;
import java.util.*;

/**
 * Classe che implementa il task che richiede al server le liste di amici e
 * di chatroom
 * @author Giulia Fois, Laura Bussi
 */
public class ClientListUpdater implements Runnable {
	
        /**
         * Metodo costruttore della classe
         */
        public ClientListUpdater() {}

	/**
         * Implementazione del task
         */
        @Override
        public void run() {
            
            //le richieste vengono mandate ogni 3 secondi
            while(true) {
                Vector<String> friends = new Vector<String>(); //vettore in cui sarà salvata la lista amici
                
                
                try {
                    //invio della richiesta lista amici
                    JSONObject reqFriendsObj = new JSONObject();
                    reqFriendsObj.put("OP", RequestType.LISTFRIEND);
                    reqFriendsObj.put("username", LoggedUserFrame.username);
                    ClientMainJFrame.writeRequest(reqFriendsObj.toString());
                    
                    //lettura della risposta
                    String reply = ClientMainJFrame.readReply();
                    JSONObject replyFriendObj = new JSONObject(reply);
                    if (replyFriendObj.getString("replycode").equals(ReplyType.OK.name())) { //la richiesta è andata a buon fine
                        int friendsNumber = replyFriendObj.getInt("numberOfFriends"); //numero di amici
                        if (friendsNumber > 0) {
                            JSONArray list = replyFriendObj.getJSONArray("friendlist"); //lista di amici mandata dal server
                            for (int i = 0; i < friendsNumber; i++) {
                                String friend = list.getString(i);
                                friends.add(friend);
                                }
                            //invio della lista amici all'interfaccia grafica
                            LoggedUserFrame.updateFriendList(friends);
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                
                try {
                    Vector<String> chats = new Vector<String>(); //vettore in cui sarà salvata la lista delle chatroom
                    
                    //invio della richiesta lista chatroom
                    JSONObject reqChatsObj = new JSONObject();
                    reqChatsObj.put("OP", RequestType.CHATLIST);
                    reqChatsObj.put("username", LoggedUserFrame.username);
                    ClientMainJFrame.writeRequest(reqChatsObj.toString());
                    
                    //lettura della risposta
                    String rep = ClientMainJFrame.readReply();
                    JSONObject replyChatObj = new JSONObject(rep);
                    if(replyChatObj.getString("replycode").equals(ReplyType.OK.name())) { //la richiesta è andata a buon fine
                        int chatsNumber = replyChatObj.getInt("numberOfChats"); //numero di chatroom
                        if(chatsNumber > 0) {
                            JSONArray list = replyChatObj.getJSONArray("chatlist"); //lista di chatroom mandata dal server
                            for (int i = 0; i < chatsNumber; i++) {
                                chats.add(list.getString(i));
                            }
                        }
                        
                        //invio della lista chatroom all'interfaccia grafica
                        LoggedUserFrame.updateChatList(chats);
                    }
                } 
                catch (JSONException e) {
                    e.printStackTrace();
                }
		
                //il thread aspetta 3 secondi prima di aggiornare nuovamente le liste
                try {
                    Thread.sleep(5000);
                } 
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

