package gossipserver;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Implementazione del task eseguito dal pool di thread che avvia
 * il threadpool per la gestione dei servizi di multicast
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public class ChatroomMessageListener implements Runnable {
	
	private int port;
	
	/*Metodo costruttore
	@overview: crea un nuovo oggetto ChatroomMessageListener */
	public ChatroomMessageListener() {
		this.port = GossipServer.udpPort;
	}
	
	/*
	@overview: override del metodo run di Runnable
	Apre il DatagramSocket per l'ascolto di messaggi entranti  e istanzia un oggetto di tipo ChatroomMessageHandler,
	passandogli il socket come argomento; quindi passa l'handler al metodo execute del threadpool chatroomHandler*/
	public void run() {
		System.out.println("ChatroomListener started");
		HandlerPool chatroomHandler = new HandlerPool();
		try {
			DatagramSocket chatroomSocket = new DatagramSocket(port);
			ChatroomMessageHandler chatroomMsg = new ChatroomMessageHandler(chatroomSocket); //gli devo passare il socket
			chatroomHandler.executeHandler(chatroomMsg);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
