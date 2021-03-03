package gossipserver;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementazione del task eseguito dal pool di thread che gestisce
 * la ricezione e lo smistamento dei messaggi di multicast alle chatroom
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public class ChatroomMessageHandler extends Handler implements Runnable {
	
	private DatagramSocket socket;
	
	/* Metodo costruttore
	@overview: crea un oggetto ChatroomMessageHandler
	@param: s    il DatagramSocket per la ricezione dei messaggi di gruppo */
	public ChatroomMessageHandler(DatagramSocket s) throws NullPointerException {
		super();
                
        this.socket = s;
	}
	
	@Override
	/*
	@overview: override del metodo run di Runnable
	Il thread rimane in attesa di pacchetti UDP destinati alle chatroom 
	Quando riceve un pacchetto, lo invia all'indirizzo di multicast della chatroom */
	public void run() {
		while(true) {
			byte[] toReceive = new byte[1024];
			Chatroom chRoom;
			DatagramPacket packet = new DatagramPacket(toReceive, toReceive.length);
			JSONObject msgObj;
			InetAddress msAddr;
			try {
				synchronized(socket) {
					socket.receive(packet);
					msgObj = new JSONObject(new String(packet.getData()));
					chRoom = GossipServer.chatroomLookup(msgObj.get("groupname").toString());
					if(chRoom.isMember(msgObj.getString("sender"))) { //invio il messaggio in multicast
						MulticastSocket sock = new MulticastSocket();
						msAddr = chRoom.getMulticastAddress();
						DatagramPacket toSend = new DatagramPacket(toReceive,toReceive.length,msAddr,7000);
						sock.send(toSend);
					} //else do nothing
				}
			} catch(IOException e) {
				e.printStackTrace();
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
