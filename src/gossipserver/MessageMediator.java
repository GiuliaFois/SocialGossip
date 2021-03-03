/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gossipserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Implementazione del task per la ricezione di messaggi destinati a chat private
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public class MessageMediator implements Runnable {
    Socket msgSock;
    BufferedReader msgReader;
    
	/**
	* Metodo costruttore
	* @overview istanzia un oggetto MessageMediator
	* @param s     il socket su cui attendere messaggi entranti
	*/
    public MessageMediator(Socket s) {
        this.msgSock = s;
        
           
    }
    
	/**
	* @oiverview override del metodo run
	* MessageMediator rimane in attesa di messaggi inviati sul soket msgSock 
	* quando riceve un messaggio, istanzia un oggetto MessageHandler e affida il task al
	* threadpool competente */
    public void run()  { 
       HandlerPool requestHandler = new HandlerPool();
        try {
               msgReader = new BufferedReader(new InputStreamReader(msgSock.getInputStream()));
           } 
            
           catch (IOException ex) {
               ex.printStackTrace();
           }
        
       
       while(true) {
           //apro il lettore
           try {
               //leggo la richiesta e la passo ad un thread del pool
               
               String message = msgReader.readLine();
               MessageHandler req = new MessageHandler(msgSock, message);
               requestHandler.executeHandler(req);
               
           }
           catch(SocketException e) {
                if(msgReader != null) {
                  try{
                      msgReader.close();
                      break;
                  }
                  catch(IOException ex) {
                      ex.printStackTrace();
                  }
           }
           }
           catch(IOException e) {
               
           }
       }
       return;
    }
         
          
               
}
           
            
         
     
   
    

