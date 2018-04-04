package multithreadfileserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * File Server Client Thread. This client Thread opens input and output streams for 
 * a specific file operation on server. Performs the operation requested by the client 
 * and returns response.
 */

public class ClientConnectionThread extends Thread {
    private BufferedReader inputReader;
    private PrintWriter outputwriter;
    private Socket clientSocket;
    private FileOperations fileOperationsUtil;


    /*
     * Creating input and output streams for the client and initiate a new
     * thread
     */
    public ClientConnectionThread(Socket clientSocket, ReadWriteLock readWriteLock) {
        this.clientSocket = clientSocket;
        this.fileOperationsUtil = new FileOperations(readWriteLock);
        try {
            this.inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outputwriter = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void startOperation() {
        try {
            System.out.println("inside start operation");
            this.start();
        } catch (Exception e) {
            System.out.println("Unable to start thread.");
        }
    }

    public void run() {
        try {
            // read input from client
            String inputLine = inputReader.readLine();

            // parse the client input
            String[] splitLines = inputLine.split("\\|");
            String operationType = splitLines[0].toUpperCase();
            String fileName = System.getProperty("user.dir") + File.separator + splitLines[1];
            String fileContent = (splitLines.length < 3) ? "" : splitLines[2];

            // perform the operation requested by client
            String output = doOperation(operationType, fileName, fileContent);
            outputwriter.println(output);
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } finally {
            try {
                inputReader.close();
                clientSocket.close();
            } catch (IOException e) {
                // close failed
            }
        }
    }
    
    public String doOperation(String operationType, String fileName, String fileContent) {
        String output = null;
        switch (operationType) {
        case "R":
            output = fileOperationsUtil.readFile(fileName);           
            break;
        case "W":
            output = fileOperationsUtil.writeFile(fileName, fileContent);
            break;
        case "D":
            output = fileOperationsUtil.deleteFile(fileName);
            break;
        default:
            System.out.println("Incorrect Command Received");
            output = "Incorrect Command";
        }
        return output;
    }
}
