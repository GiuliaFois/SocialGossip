package gossipserver;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementazione del task eseguito dal pool di thread che gestisce
 * lo scambio di messaggi tra utenti amici
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public class MessageHandler extends Handler implements Runnable {
	
	private Socket socket;
        private String message;
	
	/**
	* Metodo costruttore 
	* @overview crea un oggetto MessageHandler 
	* @param s          il socket su cui inviare i messaggi
	* @param message    il messaggio da verificare e inoltrare
	*/
	public MessageHandler(Socket s, String message) throws NullPointerException {
		super();
                this.socket = s;
                this.message = message;
	}

	@Override
	/**
	* @overview: override del metodo run di Runnable
	* Il task riceve un messaggio e lo gira all'utente destinatario, se questo è online
	*
	*/
	public void run() {
		int rd = 0;
		int off = 0;
		char[] charArr;
		JSONObject jsonMsg;
		String sender;
		String receiver;
		String content;
		String reply;
		InetAddress receiverAddr;
		int receiverPort;
                BufferedWriter messageWriter;
                BufferedWriter replyWriter;
		
		
		try {
			jsonMsg = new JSONObject(message);
			sender = jsonMsg.getString("sender");
			receiver = jsonMsg.getString("receiver");
			content = jsonMsg.getString("body");
			char[] ackBuf = new char[4];
			//ho i campi del messaggio, controllo che il destinatario sia online
			if(GossipServer.userTable.get(receiver).getStatus() == 0) reply = ReplyType.FRIENDOFFLINE.name();
			else {
                                //parte dei file
                                reply = ReplyType.OK.name();
                                System.out.println("MESSAGE HANDLER: Receiver è " + receiver);
                                String senderLanguage = GossipServer.userTable.get(sender).getLanguage();
                                String receiverLanguage = GossipServer.userTable.get(receiver).getLanguage();
                                if(senderLanguage.equals(receiverLanguage)) {
                                    JSONObject notTranslatedMessage = new JSONObject();
                                    notTranslatedMessage.put("sender", sender);
                                    notTranslatedMessage.put("receiver", receiver);
                                    notTranslatedMessage.put("body", content);
                                    notTranslatedMessage.put("OP", "MESSAGE");
                                    message = notTranslatedMessage.toString();
                                }
                                else {
                                   //mando la richiesta di traduzione
                                    String restReqStart = GossipServer.restReq;
                                    String restReq = restReqStart + spaceTo20Percent(content) + "!&langpair=";
                                    restReq = restReq + senderLanguage + "|" + receiverLanguage;
                                    System.out.println("Rest request: " + restReq);
                                    URL urlReq = new URL(restReq);
                                    InputStream stream = urlReq.openStream();
                                    byte[] byteArr = new byte[8196];
                                    stream.read(byteArr);
                                    String restReply = new String(byteArr);
                                    System.out.println(restReply);
                                    JSONObject restReplyObj = new JSONObject(restReply);
                                    JSONObject fieldObj = (JSONObject) restReplyObj.get("responseData");
                                  
                                    String translatedBody = fieldObj.getString("translatedText");
                                    JSONObject translatedMessage = new JSONObject();
                                    translatedMessage.put("sender", sender);
                                    translatedMessage.put("receiver", receiver);
                                    translatedMessage.put("body", translatedBody);
                                    translatedMessage.put("OP", "MESSAGE");
                                    message = translatedMessage.toString(); 
                                }
                                    
                                
                                Socket receiverSocket = GossipServer.userTable.get(receiver).getMessageSocket();
                                messageWriter = new BufferedWriter(new OutputStreamWriter(receiverSocket.getOutputStream()));
                                messageWriter.write(message);
                                messageWriter.newLine();
                                messageWriter.flush();
                            }
                        
                        JSONObject replyObj = new JSONObject();
                        replyObj.put("replycode", reply);
                        System.out.println("Risposta: " + replyObj.toString());
			replyWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        replyWriter.write(replyObj.toString());
                        replyWriter.newLine();
                        replyWriter.flush();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch(NullPointerException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			
		}
		
	return;	

	}
  
    public String spaceTo20Percent(String s) {
        
        String[] stringArray = s.split(" ");
        String result = new String();
        for(int i = 0; i < stringArray.length; i++) {
           result = result + stringArray[i];
           if(i != stringArray.length - 1) result = result + "%20";
        }
        return result;
    }

}
