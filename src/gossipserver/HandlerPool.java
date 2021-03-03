package gossipserver;


import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import org.json.*;

/**
 * Implementazione del threadpool per l'esecuzione degli handler
 * 
 * @author Giulia Fois, Laura Bussi
 *
 **/
public class HandlerPool {
	private ThreadPoolExecutor executor;
	
	/**
	* Metodo costruttore
	* @overview crea un nuovo oggetto HandlerPool come CachedThreadPool
	*/
	public HandlerPool() {
		this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
	}
	
	/**
	* @overview esegue l'handler h passato come argomento
	* @param h   l'handler da eseguire
	*/
	public void executeHandler(Handler h) {
		executor.execute(h);
	}

} 
