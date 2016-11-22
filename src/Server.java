import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-20
 * Last Updated on: 2016-11-20
 * Filename: DiscoveryThread
 * Description: Executes the server side code
 *      Socket code from the official java tutorial:
 *      http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */

public class Server extends SwingWorker<Void, Void> {
    private int portNumber = 8888;
    private boolean listening = true;
    private ServerSocket serverSocket;
    private static ConcurrentHashMap <String, String> leaderboard = new ConcurrentHashMap<>();
    private static AtomicInteger loaded = new AtomicInteger();
    private static AtomicInteger numClients = new AtomicInteger();
    private BoggleGame game = new BoggleGame();

    public Server(int portNum, WordTrie dictionary) {
        this.portNumber = portNum;
        HashSet<String> wordSet = game.findAllGameWords(dictionary);
        leaderboard= new ConcurrentHashMap<>();
        loaded.set(0);
        numClients.set(0);
        String wordSetString = "";

        for (String word : wordSet)
            wordSetString += word + "-";

        new GameProtocol(this.game, wordSetString);
    }

    public void startServer () {

        try {
            ConnectionThread.reset();
            listening = true;
            serverSocket = new ServerSocket(portNumber);
            serverSocket.setSoTimeout(2);
            System.out.println("Server Initialized");

            // Listen for a client, create a thread to handle it
            while (listening) {
//                System.out.println("Server listening on: "+ portNumber);
                try {
                    new ConnectionThread(serverSocket.accept()).start();
                    numClients.incrementAndGet();
                } catch (Exception e) {

                }
            }

            serverSocket.close();
            System.out.println("Closed server");
        } catch (IOException ioerr) {
            // Couldn't connect to socket or socket closed
//            ioerr.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground() throws Exception {
        startServer();
        return null;
    }

    public void closeServer() {
        listening = false;
    }

    public static void updateKey(String key, String value){
        Server.leaderboard.put(key, value);
    }

    public static int clientLoaded() {
        return Server.loaded.incrementAndGet();

    }

    public static int getNumClients(){
        return Server.numClients.get();
    }

    public static String getScores () {
        String scores = "";
        Iterator it = Server.leaderboard.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            scores += pair.getKey() + ":" + pair.getValue() + ",";
        }
        return scores;
    }

}
