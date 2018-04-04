package multithreadfileserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Scanner;

/*
 * client program implementing TCP socket
 */
public class Client {
    /*
     * Reading input from Console
     */
    public void readFromConsole() {
        // Reading from System.in
        System.out.println("Operations: R == READ\n W == WRITE\n D == DELETE   ");
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter the operation number");
        // Scans the next token of the input
        String operation = reader.nextLine();
        System.out.println("Enter the Filename");
        // Scans the next token of the input
        String filename = reader.nextLine();
        String fileMessage = "";
        if (operation.equalsIgnoreCase("W")) {
            System.out.println("Enter the Message");
            // Scans the next line for file message
            fileMessage = reader.nextLine();
        }

        reader.close();
        // Making a socket connection, communicating with server
        SocketConnection(operation, filename, fileMessage);

    }

    /*
     * Making a socket connection, sending input to server via Socket, getting
     * response from server
     */
    private void SocketConnection(String Operation, String filename, String filemessage) {
        Socket socket = null;
        try {
            socket = new Socket(Constants.IP, Constants.SERVER_PORT);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String request = String.valueOf(Operation + "|" + filename + "|" + filemessage);

            // Sending input from console to server via socket
            writer.println(request);
            System.out.println("Request sent at " + new Timestamp(System.currentTimeMillis()));

            // Receiving response from server via Socket
            String outMessage = inputReader.readLine();
            System.out
                    .println("Received from Server: " + outMessage + "at" + new Timestamp(System.currentTimeMillis()));
        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        } finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    /* close failed */}
        }

    }

    public static void main(String args[]) {
        // Reading input from Console
        Client c = new Client();
        c.readFromConsole();

    }

}
