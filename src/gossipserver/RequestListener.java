package gossipserver;


import java.io.*;
import java.net.*;

/**
 * Implementazione del thread che agisce da dispatcher per le 
 * richieste di interazione con la social network inviate dagli
 * utenti
 * 
 * @author Giulia Fois, Laura Bussi
 *
 */
public class RequestListener implements Runnable {
	
    private int port; //porta del socket per le richieste

    /**
     * Metodo costruttore
     */
    public RequestListener() {
            this.port = GossipServer.requestPort;
    }

    /*
    Implementazione del task
    */
    @Override
    public void run() {

        //creazione del pool che spawna thread per la gestione delle richieste
        try {
            ServerSocket requestSocket = new ServerSocket(port);

            while(true) { 
                
                //accettazione di una nuova connessione
                Socket s = requestSocket.accept();

                /*
                spawn del thread che si occuperà di ricevere le richieste
                sul socket e di inoltrarle agli handler
                */
                RequestMediator reqMed = new RequestMediator(s);
                Thread t = new Thread(reqMed);
                t.start();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
