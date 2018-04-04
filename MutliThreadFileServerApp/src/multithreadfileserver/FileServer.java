package multithreadfileserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class responsible for Server side implementation of TCP connection
 */
public class FileServer {
    private ServerSocket serverSocket;
    private static FileServer INSTANCE = null;
    private final ReadWriteLock readWriteLock;

    // constructor is private to prevent object initialization outside class
    private FileServer() {
        readWriteLock = new ReadWriteLock();
    }

    // method for fetching/initialization of singleton object
    public static FileServer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileServer();
        }
        return INSTANCE;
    }

    /**
     * Creates a client socket and spawns off a thread to serve the client connection.
     */
    public void createSocketConnection() {
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT);
            System.out.println("Server start listening on " + Constants.SERVER_PORT);
            while (true) {
                // Creates a client socket, passes the socket to the client
                // connection which spawns a new thread.
                Socket clientSocket = serverSocket.accept();
                ClientConnectionThread clientThread = new ClientConnectionThread(clientSocket, readWriteLock);
                clientThread.startOperation();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    public static void main(String args[]) {
        FileServer fileServer = FileServer.getInstance();
        fileServer.createSocketConnection();
    }

}
