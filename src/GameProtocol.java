import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-20
 * Last Updated on: 2016-11-20
 * Filename: GameProtocol
 * Description: The protocol that the client and server use to communicate
 */
public class GameProtocol {
    private static final int WAITING = 0;
    private static BoggleGame game;
    private static String wordSet;

    public GameProtocol(BoggleGame game, String wordSet) {
        this.game = game;
        this.wordSet = wordSet;
    }

    public GameProtocol() {

    }

    // Processes
    public String processInput(String clientMsg) {
        String theOutput = "";


        if (clientMsg.startsWith("INIT")) {
            // Send the board to initialize
            Server.updateKey(clientMsg.substring(4), "0");
            theOutput = game.toString();

        } else if (clientMsg.startsWith("UPDATE")) {
            // Update the client's score
            String[] tokens = clientMsg.substring(7).split(":");
            Server.updateKey(tokens[0], tokens[1]);

        } else if (clientMsg.startsWith("FINISHED")) {
            theOutput = "FINISHED";
        } else if (clientMsg.startsWith("WORDSET")) {
            theOutput = GameProtocol.wordSet;

        } else if (clientMsg.startsWith("START")) {
            theOutput = "START";
        } else if (clientMsg.startsWith("CANSTART?")) {
            theOutput = "CANSTART?";
        } else if (clientMsg.startsWith("SCORES")){
            theOutput = Server.getScores();
        } else {
            theOutput = "ERROR";
        }

        return theOutput;
    }
}
