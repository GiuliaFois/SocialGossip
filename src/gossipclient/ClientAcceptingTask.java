package gossipclient;

import java.net.*;
import java.io.IOException;

/**
 * La classe implementa il task di accettazione della connessione sulla quale
 * verranno ricevuti i messaggi e le notifiche di file in entrata
 * @author Giulia Fois, Laura Bussi
 */
public class ClientAcceptingTask implements Runnable {
    
    ServerSocket messageServerSock; //ServerSocket su cui verrà accettata la connessione
    
    /**
     * Metodo costruttore della classe
     * @param serverSock ServerSocket su cui viene accettata la connessione
     * @throws NullPointerException se serverSock == null
     *         IllegalArgumentException se serverSock è chiuso
     */
    public ClientAcceptingTask(ServerSocket serverSock) {
        if(serverSock == null) throw new NullPointerException();
        if(serverSock.isClosed()) throw new IllegalArgumentException();
        this.messageServerSock = serverSock;
    }
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() {
        try {
           Socket receiverSock = messageServerSock.accept();
           LoginGossipClient.msgLisSock = receiverSock;
        } 
        catch (IOException e) {
            //chiusura del socket: non è gestita, termina normalmente il thread
        }
    }
}
