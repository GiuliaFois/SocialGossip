package gossipclient;


/**
 * Enumerazione di tutte le possibili tipologie di richieste inviate al Server
 * 
 * @author Giulia Fois, Laura Bussi
 */

public enum RequestType {
	REGISTER, LOGIN, LOGOUT, LOOKUP, FRIENDSHIP, LISTFRIEND, //operazioni sulla Social Network
	CREATE, ADDME, CHATLIST, CLOSECHAT, //gestione chat
	MSG2FRIEND, CHATROOM_MESSAGE, FILE2FRIEND //invio messaggi e file
	
}
