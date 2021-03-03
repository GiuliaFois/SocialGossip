package gossipserver;




import java.net.InetAddress;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe Chatroom
 * Rappresenta un gruppo di discussione a cui più utenti possono partecipare
 * pur non avendo necessariamente relazioni di amicizia
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public class Chatroom {
	private String name;
	private InetAddress ipMulticastAddr;
	private String creator;
	private CopyOnWriteArrayList<String> members;
	
	/*Metodo costruttore
	@overview: crea un nuovo oggetto di tipo chatroom
	@param: name     nome della chatroom
	@param: creator  creatore della chatroom
	@param: ipAddr   indirizzo di multicast della chatroom */
	public Chatroom(String name,String creator,InetAddress ipAddr) {
		this.name = name;
		this.ipMulticastAddr = ipAddr;
		this.creator = creator;
		this.members = new CopyOnWriteArrayList<String>();
		members.add(creator);
	}
	
	/*
	@overview: restituisce il nome della chatroom */
	public String getName() {
		return this.name;
	}
	
	/*
	@overview: restituisce una copia della lista degli iscritti alla chatroom */
	public Vector<String> getMembers() {
		return new Vector<String>(this.members);
	}

	/*
	@overview: restituisce true se l'utente "username" è iscritto alla chatroom 
	@param username   il nickname dell'utente da verificare */
	public boolean isMember(String username) {
		return members.contains(username);
	}

	/*
	@overview: aggiunge un utente alla chatroom
	@param username    il nickname dell'utente da aggiungere */
	public void addMember(String username) {
		members.add(username);
	}
	
	/*
	@overview: restituisce l'indirizzo di multicast della chatroom */
	public InetAddress getMulticastAddress() {
		return this.ipMulticastAddr;
	}
	
	/*
	@overview: restituisce true se l'utente "username" è il creatore (amministratore) della chatroom, false altrimenti
	@param: username    il nickname dell'utente da verificare */
	public boolean isCreator(String username) {
		return username.equals(this.creator);
	}

	/*
	@overview: rimuove tutti gli utenti dalla chatroom, se il richiedente è amministratore
	@param: username    il nickname dell'utente richiedente */
	public void removeAllUsers(String username) {
		if(this.isCreator(username)) {
			User tmp = null;
			for(int i = 0; i<members.size(); i++) {
				tmp = GossipServer.userLookup(members.get(i));
				//invia callback via RMI
				tmp.removeGroup(this.name);
			}
		}		
	}
}
