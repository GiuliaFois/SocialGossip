package gossipclient;

import java.io.*;
import java.net.*;
import org.json.*;
import java.awt.event.*;
import java.nio.channels.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * La classe implementa il task in ascolto sul socket dedicato alla 
 * ricezione di messaggi o file 
 * @author Giulia Fois, Laura Bussi
 */
public class ClientListener implements Runnable {
	
    Socket listenerSocket; //socket su cui il thread riceve le notifiche di messaggi o file
    BufferedReader reader; //reader collegato al socket
    int filePort; //porta sulla quale il thread riceverà i file

    /**
     * Metodo costruttore
     * @param lisSock Socket in ricezione
     * @throws NullPointerException se lisSock == null
     */
    public ClientListener(Socket lisSock) {
        if(lisSock == null) throw new NullPointerException();
        this.listenerSocket = lisSock;
    }
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() {
        while(true) {
            String body; //corpo del messaggio
            String op; //file o msg
            JSONObject msgObj;
            try {
                //il thread è bloccato in ricezione di un messaggio da parte del server
                final String sender; //sender del messaggio
                String message = LoggedUserFrame.readMessage();
                msgObj = new JSONObject(message);
                sender = msgObj.getString("sender");
                op = msgObj.getString("OP");
                if(op.equals("MESSAGE")) { //è stato ricevuto un messaggio
                    body = msgObj.getString("body");
                    if(LoggedUserFrame.containsFriendChatFrame(sender)) { //il frame della chat è già attivo
                    //il messaggio viene appeso sul log della chat
                    FriendChatJFrame frame = LoggedUserFrame.getFriendChatFrame(sender);
                    String friendChatUsername = "<" + sender + ">";
                    frame.updateChatLog(friendChatUsername + ": " + body);
                    }
                    else { //il frame della chat non è attivo: viene aperto
                        FriendChatJFrame friendChatFrame = new FriendChatJFrame(sender);
                        friendChatFrame.setVisible(true);
                        LoggedUserFrame.addFriendChatFrame(sender, friendChatFrame);
                        friendChatFrame.addWindowListener(new WindowAdapter() {
                            /*
                            handler che rimuove il frame dalla lista di quelli attivi
                            */
                            @Override
                            public void windowClosing(WindowEvent evt) {
                                        LoggedUserFrame.removeFriendChatFrame(sender);
                                    }
                                });
                        //il messaggio viene appeso sul log della chat
                        String friendChatUsername = "<" + sender + ">";
                        friendChatFrame.updateChatLog(friendChatUsername + ": " + body);
                    }
                }
                else if(op.equals("FILE")) {
                    //ServerSocketChannel su cui verrà ricevuto il file
                    ServerSocketChannel fileServerSocket = null;
                    int fileSize = msgObj.getInt("fileSize"); //dimensione del file
                    String fileName = msgObj.getString("fileName"); //nome del file
                    //messaggio da appendere al log della chat
                    String fileMessage = "L'utente " + sender + " vuole mandarti un file";
                    if(LoggedUserFrame.containsFriendChatFrame(sender)) { //il frame della chat è attivo
                        FriendChatJFrame frame = LoggedUserFrame.getFriendChatFrame(sender);
                        //appendo il messaggio di notifica al log della chat
                        frame.updateChatLog(fileMessage);
                    }
                    else { //il frame della chat non è attivo: viene aperto
                        FriendChatJFrame friendChatFrame = new FriendChatJFrame(sender);
                        friendChatFrame.setVisible(true);
                        LoggedUserFrame.addFriendChatFrame(sender, friendChatFrame);
                        friendChatFrame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent evt) {
                                LoggedUserFrame.removeFriendChatFrame(sender);
                            }
                        });
                        //appendo il messaggio di notifica al log della chat
                        friendChatFrame.updateChatLog(fileMessage);
                    }
                    //portscan per trovare una porta libera su cui ricevere il file
                    for(int i=1025; i<65536; i++) {
                        try {
                            filePort = i;
                            fileServerSocket = ServerSocketChannel.open();
                            fileServerSocket.bind(new InetSocketAddress(filePort));
                            break;
                        } 
                        catch(IOException e) {
                           
                        } 
                    }
                    //avvio del thread che si occupa della ricezione vera e propria del file
                    FileReceiverTask task = new FileReceiverTask(fileServerSocket, fileSize, fileName, sender);
                    Thread t = new Thread(task);
                    t.start();
                    //invio al server di indirizzo IP e porta file
                    JSONObject fileSocketData = new JSONObject();
                    fileSocketData.put("filePort", filePort);
                    try( BufferedWriter messageWriter = 
                        new BufferedWriter(new OutputStreamWriter(listenerSocket.getOutputStream()));) {
                        messageWriter.write(fileSocketData.toString());
                        messageWriter.newLine();
                        messageWriter.flush();
                    }
                    catch(SocketException e) {
                        //problemi con il socket: avvio la procedura di logout
                        LogoutTask logOut = new LogoutTask();
                        Thread logOutThread = new Thread(logOut);
                        logOutThread.start();
                    }
                    catch(IOException ex) {
                       
                    }
                }
            } 
            catch (JSONException ex) {
                //non gestisco l'eccezione, riprende l'esecuzione dal ciclo while
            }
        }
    }
}
