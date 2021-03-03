package gossipclient;

/**
 * Enumerazione di tutti i possibili tipi di risposta da parte del Server
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public enum ReplyType {
	OK, FAIL, USRALREADY, USRNOEXISTS, ONLINEALREADY, USRFOUND, USRNOTFOUND, NOFRIENDS,
	FOREVERALONE, ALREADYFRIENDS, WRONGPWD, FRIENDOFFLINE, NOTLOGGED, CHATALREADY
}
