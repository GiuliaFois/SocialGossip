package gossipserver;

import java.net.*;
import java.io.*;

/**
 * Classe che implementa il task che riceve richieste
 * sul socket dedicato e, mediante un pool, spawna
 * thread handler che le gestiscano
 * @author Giulia Fois, Laura Bussi
 */
public class RequestMediator implements Runnable {
    
    Socket reqSock;
    BufferedReader reqReader;
    
    /**
     * Metodo costruttore
     * @param s Socket richieste di un client specifico
     */
    public RequestMediator(Socket s) {
        this.reqSock = s;
    }
    
    /*
    Implementazione del task
    */
    @Override
    public void run() { 
        
        //handler per spawnare i thread gestori
        HandlerPool requestHandler = new HandlerPool();
        try {
            //apertura del reader sul socket
            reqReader = new BufferedReader(new InputStreamReader(reqSock.getInputStream()));
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //ricezione delle richieste di un particolare client
        while(true) {
           
            try {
                //lettura della richiesta e spawn del thread gestore
                String request = reqReader.readLine();
                RequestHandler req = new RequestHandler(request, reqSock);
                requestHandler.executeHandler(req);
            } 
            catch(SocketException e) { //chiusura del socket in caso di problemi
                if(reqReader != null) {
                    try{
                      reqReader.close();
                      break;
                    }
                    catch(IOException ex) {
                      ex.printStackTrace();
                    }
                }
            }
            catch (IOException ex) {
               ex.printStackTrace();
            }
        }
    }
}
    

