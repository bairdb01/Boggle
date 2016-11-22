import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-20
 * Last Updated on: 2016-11-20
 * Filename: Client
 * Description: Client to talk to server
 * Based on the java tutorial:
 *      http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */
public class Client {
    String hostName = "";
    int portNumber = 8888;
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    String name;
    int score = 0;

    public Client (String hostName, int portNumber, String name){
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.name = name;

        try {
            // Open a socket to the server
            clientSocket = new Socket(hostName, portNumber);

            // Established the i/o of the socket
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException ioerr) {
            // Error creating socket
            ioerr.printStackTrace();
        }
    }

    public String talkToServer (String msg) {
        // Communicate with server
        String fromServer = "";
        try {

            if (msg.startsWith("UPDATE")) {
                String [] tokens = msg.split(",");
                this.score += Integer.parseInt(tokens[1]);
                out.println("UPDATE" + "," + name + ":" + this.score);
            } else {
                out.println(msg + name);
            }

            fromServer = in.readLine();

            if (fromServer.equals("FINISHED")) {
                return fromServer;
            }

//            System.out.println("("+ msg+ ") Server Response: " + fromServer);
//            }
        } catch (IOException ioerr) {

        }
        return fromServer;
    }

    public void closeSocket(){
        try {
            clientSocket.close();
        } catch (IOException ioerr) {

        }
    }
}
