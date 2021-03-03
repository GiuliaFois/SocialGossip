package gossipserver;


import java.io.IOException;
import java.net.*;

/**
 * Implementazione del task per l'accettazione di nuove connessioni relative a chat private
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public class MessageListener implements Runnable {
	private int port;
	
	/*
	* Metodo costruttore
	* @overview crea un oggetto MessageListener
	*/
	public MessageListener() {
		this.port = GossipServer.listenerPort;
	}
	
	/*
	* @overview override del metodo run
	* MessageListener rimane in attesa di connessioni entranti
	* quando viene accettata una nuova connessione, si istanzia un MessageMediator
	* che riceverà i messaggi entranti sul socket passato come argomento
	*/
	public void run() {
		System.out.println("MessageListener started");
		//apro due socket su due porte diverse
		//creazione del pool gestore delle richieste
		HandlerPool messageHandler = new HandlerPool();
		try {
			ServerSocket messageSocket = new ServerSocket(port);
			while(true) { 
                                Socket s = messageSocket.accept();
                                System.out.println("DOPO ACCEPT: " + s);
                                //mando un thread "mediatore" che si occupa di spawnare ad ogni
                                //richiesta un nuovo thread del pool
				MessageMediator msgMed = new MessageMediator(s);
                                Thread t = new Thread(msgMed);
                                t.start();
				}
		} catch (IOException e) {
			System.out.println("Socket creation: error");
			e.printStackTrace();
			return;
		}
		

	}

}
