import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-20
 * Last Updated on: 2016-11-20
 * Filename: ConnectionThread
 * Description: A thread to handle a server request
 *  Based on the code from the java tutorial
 *      http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */

public class ConnectionThread extends Thread{
    private Socket socket;
    private static boolean canStart;
    private static boolean finished;
    public ConnectionThread(Socket socket){
        super("ConnectionThread");
        this.socket = socket;
    }

    public void run() {
        try {
            // Gets the socket's i/o stream and opens readers/writers on them
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine, outputLine;

            // Initiate conversation with client
            GameProtocol gameProtocol = new GameProtocol();

            // Communicate with the client by reading/writing to the socket
            while((inputLine = in.readLine()) != null ){
                // Get proper response to clients message
                outputLine = gameProtocol.processInput(inputLine);

                if (outputLine.equals("START")) {
                    canStart = true;
                } else if (canStart && outputLine.equals("CANSTART?")){
                    outputLine = "START";
                }

                if (outputLine.equals("FINISHED") || finished) {
                    outputLine = "FINISHED";
                    finished = true;
                    out.println(outputLine);
                    break;
                }

                out.println(outputLine);
            }
            socket.close();
        } catch(IOException ioerr) {
            ioerr.printStackTrace();
        }
    }
    public static void reset(){
        ConnectionThread.canStart = false;
        finished = false;
    }

}
