package gossipclient;

import java.io.*;
import java.nio.*;
import java.nio.file.Paths;
import java.nio.channels.*;
import java.util.regex.Pattern;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.nio.file.StandardOpenOption;

/**
 * Classe che implementa il task di ricezione di un file
 * @author Giulia Fois, Laura Bussi
 */
public class FileReceiverTask implements Runnable {
    
    ServerSocketChannel fileReceiverSock; //variabile che conterrà il ServerSocketChannel su cui accettare la connessione
    int fileSize; //variabile dove verrà salvata la dimensione del file
    String filePath; //variabile dove verrà salvato il path del file (nel file system del mittente)
    String fileSender; //mittente del file
    /*
    separatore dei path del file system: può essere diverso a seconda del sistema operativo
    su cui gira il programma
    */
    String fileSeparator;
    String[] fileNameTokens; //path tokenizzato, usato per estrarre il nome del file
    String fileName; //conterrà il nome del file
    String downloadedFilePath; //path nel file system del destinatario
    
    
    /**
     * 
     * @param serverSock Il server socket su cui accettare la connessione da cui ricevere il file
     * @param size La dimensione del file da ricevere (in byte)
     * @param path Il path del file che verrà ricevuto
     * @param sender Il mittente del file
     * @throws NullPointerException se serverSock == null o name == null o sender == null
     *         IllegalArgumentException se la dimensione o il nome del file non sono validi
     */
    public FileReceiverTask(ServerSocketChannel serverSock, int size, String path, String sender) {
        
        if(serverSock == null || path == null || sender == null) throw new NullPointerException();
        if(size < 0 || path.equals("")) throw new IllegalArgumentException();
        this.fileReceiverSock = serverSock;
        this.fileSize = size;
        this.filePath = path;
        this.fileSender = sender;
        fileSeparator = Pattern.quote(File.separator);
        //estrazione del nome del file
        fileNameTokens = filePath.split(fileSeparator);
        fileName = fileNameTokens[fileNameTokens.length - 1];
        downloadedFilePath = LoggedUserFrame.filesDirPath + "\\" + fileName;
    }
    
    /**
     * Implementazione del task
     */
    @Override
    public void run() {
        /*
        try-with-resources per l'accettazione della connessione su cui leggere il file
        per la creazione del file nel file system del destinatario
        */
        try(
            SocketChannel sock = fileReceiverSock.accept();
            FileChannel file = FileChannel.open(Paths.get(downloadedFilePath), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            ) {
            
            int read = 0; //numero di byte ricevuti durante la lettura del file
            ByteBuffer buf = ByteBuffer.allocateDirect(1024); //buffer su cui saranno allocati i chunks del file
            
            //lettura del file dal socket e scrittura sul file appena aperto
            while(read < fileSize) {
                int tempRead = sock.read(buf);
                read = read + tempRead;
                buf.flip();
                file.write(buf);
                buf.clear();
            }
            
        }
        catch (IOException e) {
            /*
            il file è stato chiuso dall'altro lato o ci sono stati problemi con il socket
            data la try-with-resources verranno chiusi anche da questo lato
            */
            return;
        }
        
        //notifica di avvenuta ricezione del file
        String fileReceivedMessage = "E' stato ricevuto il file " + fileName;
        /*
        se il frame è già attivo sulla schermata (quindi è presente nella lista dei frames attivi di LoggedUserFrame)
        viene semplicemente fatta una append sul log della chat
        */
        if(LoggedUserFrame.containsFriendChatFrame(fileSender)) {
            FriendChatJFrame frame = LoggedUserFrame.getFriendChatFrame(fileSender);
            frame.updateChatLog(fileReceivedMessage);
        }
        /*
        altrimenti avviene il pop-up del frame della chat con il sender del file
        */
        else {
            FriendChatJFrame friendChatFrame = new FriendChatJFrame(fileSender);  
            friendChatFrame.setVisible(true);
            LoggedUserFrame.addFriendChatFrame(fileSender, friendChatFrame);
            friendChatFrame.addWindowListener(new WindowAdapter() {
                //handler chiusura frame: rimuove il frame da quelli attivi
                @Override
                public void windowClosing(WindowEvent evt) {
                    LoggedUserFrame.removeFriendChatFrame(fileSender);
                }
            });
        } 
    }
}
