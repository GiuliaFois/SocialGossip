package gossipclient;

import java.io.*;
import org.json.*;
import java.net.*;
import java.awt.event.*;


/**
 * Classe che implementa il task di ricezione di messaggi
 * multicast e aggiornamento del frame della relativa chatroom
 * @author Giulia Fois, Laura Bussi
 */
public class ClientMulticastManager implements Runnable {
    
    MulticastSocket chatroomSocket; //socket multicast
    
    /**
     * Metodo costruttore
     * @param sock Socket multicast
     * @throws NullPointerException Se sock == null
     */
    public ClientMulticastManager(MulticastSocket sock) {
        if(sock == null) throw new NullPointerException();
        this.chatroomSocket = sock;
    }
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() {
        while(true) {
            byte[] buffer = new byte[1024];
                try {
                    final String groupname; //nome del gruppo a cui è inviato il pacchetto
                    String sender; //sender del messaggio
                    String body; //corpo del messaggio
                    
                    //ricezione del pacchetto
                    DatagramPacket pack = new DatagramPacket(buffer,buffer.length);
                    try {
                        chatroomSocket.receive(pack);
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                    String msg = new String(buffer, 0, pack.getLength());
                    JSONObject msgObj = new JSONObject(msg);
                    ChatroomJFrame chFrame;
                    groupname = msgObj.getString("groupname");
                    sender = msgObj.getString("sender");
                    body = msgObj.getString("body");
                    
                    /*
                    caso in cui il pacchetto sia di questo utente:
                    il pacchetto è ignorato
                    */
                    if(sender.equals(LoggedUserFrame.username))
                        continue;
                    
                    //caso in cui il frame della chatroom sia già aperto
                    if(LoggedUserFrame.chatroomFrames.containsKey(groupname)) 
			chFrame = LoggedUserFrame.getChatroomFrame(groupname);
                    
                    //caso in cui il frame della chatroom non è già aperto: viene aperto
                    else {
			chFrame = new ChatroomJFrame(groupname);
                        LoggedUserFrame.addChatroomFrame(groupname,chFrame);
                        chFrame.setVisible(true);
                        chFrame.addWindowListener(new WindowAdapter() {
                            /*
                            handler che rimuove il frame dalla lista di quelli
                            attivi alla sua chiusura
                            */
                            @Override
                            public void windowClosing(WindowEvent evt) {
                                LoggedUserFrame.removeChatroomFrame(groupname);

                            }
                        });
                    }
                    
                    //append del messaggio sul log della chatroom
                    String friendChatUsername = "<" + sender + ">";
                    chFrame.updateChatLog(friendChatUsername + ": " + body);
		}
                catch(JSONException e) {
                    e.printStackTrace();
                }
            }
	}
    }
