package gossipclient;

import java.io.*;
import java.rmi.*;
import java.net.*;
import org.json.*;
import java.awt.event.*;
import javax.swing.JFrame;
import java.rmi.registry.*;
import gossipserver.ServerRemoteInterface;
import java.rmi.server.UnicastRemoteObject;

/**
 * La classe implementa la procedura di login del client
 * @author Giulia Fois, Laura Bussi
 */
public class LoginGossipClient implements Runnable {
    
    String username; //nickname dell'utente
    String password; //password dell'utente
    static InetAddress serverAddr; //indirizzo del server
    int msgLisPort; //porta del socket di ricezione di messaggi e file
    int msgWriPort; //porta del server per i messaggi in uscita
    int chatroomPort = 7000; //porta multicast
    int RMIPort; //porta del servizio RMI
    static ServerSocket lisSock = null; //su cui sarà accettata la richiesta di connessione del server
    static MulticastSocket msSocket; //socket multicast
    static Socket msgLisSock = null; //socket di ricezione di messaggi e file
    static Socket msgWriSock = null; //socket di invio di messaggi
    static Socket reqRepSock = null; //socket di invio di richieste
    JFrame initFrame; //istanza del frame di registrazione
    
    /**
     * Metodo costruttore
     * @param nick Username dell'utente
     * @param pass Password dell'utente
     * @param frame Istanza del frame di registrazione (da chiudere in caso di successo del login)
     */
    public LoginGossipClient(String nick, String pass, JFrame frame) {
		this.username = nick;
                this.password = pass;
		this.initFrame = frame;
                try {
                    this.msSocket = new MulticastSocket(chatroomPort);
                } 
                catch(SocketException e) {
                    LogoutTask logOut = new LogoutTask();
                    Thread logOutThread = new Thread(logOut);
                    logOutThread.start();
                }
                catch (IOException e) {
                    LogoutTask logOut = new LogoutTask();
                    Thread logOutThread = new Thread(logOut);
                    logOutThread.start();
                }           
            }
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() {
	
        //portscan per l'apertura del ServerSocket su cui accettare la richiesta di connessione
        for(int i = 1025; i < 65536; i++) {
            msgLisPort = i;
            try {
                lisSock = new ServerSocket();
                lisSock.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), msgLisPort));
                break;
            } 
            catch(BindException e) {
                //non viene gestita, si riprova la bind su un'altra porta
            }
            catch(IOException e) {
                
            }    
        }
        
        /*
        spawn del thread che si occuperà di accettare a tutti gli effetti la connessione
        sul ServerSocket
        */
        ClientAcceptingTask aT = new ClientAcceptingTask(lisSock);
        Thread t = new Thread(aT);
        t.start();
        
        //invio della richiesta di login al server
        JSONObject loginReq = new JSONObject();
        try {
            loginReq.put("OP",RequestType.LOGIN);
            loginReq.put("username", this.username);
            loginReq.put("password", this.password);
            loginReq.put("msgIn",msgLisPort);
        }
        catch(JSONException e) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile eseguire il login. Riprova");
            popUpMessage.setVisible(true);
            return;
        }
        ClientMainJFrame.writeRequest(loginReq.toString());
        
        //lettura della risposta
        String reply = ClientMainJFrame.readReply();
        String replycode; //codice di risposta del server
        JSONObject replyObj;
        try {
            replyObj = new JSONObject(reply);
            replycode = replyObj.getString("replycode");
        }
        catch(JSONException e) {
            PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile eseguire il login. Riprova");
            popUpMessage.setVisible(true);
            closeListenerSocket();
            return;
        }
        
        //possibili errori dell'utente rispetto al login
        if(replycode.equals(ReplyType.USRNOEXISTS.name())) { //login di un utente che non esiste
            PopUpJFrame popUpMessage = new PopUpJFrame("L'utente non esiste. Riprova");
            popUpMessage.setVisible(true);
            closeListenerSocket();
            return;
        }
        else if(replycode.equals(ReplyType.WRONGPWD.name())) { //password errata
            PopUpJFrame popUpMessage = new PopUpJFrame("La password è errata. Riprova");
            popUpMessage.setVisible(true);
            closeListenerSocket();
            return;
        }   
        else if(replycode.equals(ReplyType.ONLINEALREADY.name())) { //login di un utente già connesso
            PopUpJFrame popUpMessage = new PopUpJFrame("L'utente è già online. Riprova");
            popUpMessage.setVisible(true);
            closeListenerSocket();
            return;
        }
        else if(replycode.equals(ReplyType.OK.name())) { //il login è andato a buon fine
            try {
                RMIPort = replyObj.getInt("rmi");
                msgWriPort = replyObj.getInt("msgIn");
            }
            catch(JSONException e) {
                PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile eseguire il login. Riprova");
                popUpMessage.setVisible(true);
                closeListenerSocket();
                return;
            }
            
            //registrazione al servizio RMI
            ServerRemoteInterface server = null;
            try {
                Registry registry = LocateRegistry.getRegistry(RMIPort);
                server = (ServerRemoteInterface) registry.lookup("RemoteServer");
            } 
            catch(RemoteException ex) {
                PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile eseguire il login. Riprova");
                popUpMessage.setVisible(true);
                closeListenerSocket();
                return;
            }
            catch (NotBoundException ex) {
                PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile eseguire il login. Riprova");
                popUpMessage.setVisible(true);
                closeListenerSocket();
                return;
            } 
            try {
                ClientRemoteInterface callbackObj = new ClientRemoteImpl();
                ClientRemoteInterface stub;
                stub = (ClientRemoteInterface) UnicastRemoteObject.exportObject(callbackObj,0);
                server.register(username,stub);
            } 
            catch(RemoteException e) {
                PopUpJFrame popUpMessage = new PopUpJFrame("Impossibile eseguire il login. Riprova");
                popUpMessage.setVisible(true);
                closeListenerSocket();
                return;
            }
            
            //chiusura del frame di registrazione
            initFrame.setVisible(false);
            initFrame.dispose();
            
            //apertura del frame per gli utenti loggati
            LoggedUserFrame loggedFrame = new LoggedUserFrame(msgLisSock, username, msgLisPort, msgWriPort);
            loggedFrame.setVisible(true);
            loggedFrame.addWindowListener(new WindowAdapter() {
                //handler: alla chiusura del frame viene mandata anche la richiesta di logout
                @Override
                public void windowClosing(WindowEvent evt) {
                   LogoutTask logOut = new LogoutTask();
                    logOut.run();
                }
            });
        }
    }

/**
 * Metodo di cleanup per chiudere il ServerSocket della connessione
 * messaggi in caso di errore
 */
public static void closeListenerSocket() {
    if(!lisSock.isClosed() && lisSock != null) {
        try {
            lisSock.close();
        }
        catch(IOException e) {
        }
    }
  }

}
