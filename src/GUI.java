import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Timer;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-18
 * Last Updated on: 2016-11-18
 * Filename: GUI
 * Description: The GUI for the boggle game
 *  Multiplayer
 * TODO: Add multiplayer : host starts the game when ready
 *
 * Singleplayer
 * TODO: Allow n x n boards
 *
 * Both
 * TODO: Game settings menu
 */

public class GUI{
    static BoggleGame bg;
    static JButton [] letterBtns;
    static WordTrie dictionary = new WordTrie();
    static HashSet<String> wordSet;
    static ArrayList<Cell> selected;
    static JTextArea headerText;
    static JFrame game;
    static JTabbedPane mainArea;
    static JPanel singlePlayer;
    static JPanel multiplayer;
    static JPanel header;
    static JLabel remaining;
    static JTextArea usrAnswers;
    static JPanel playSpace;
    static JScrollPane scrollAnswers;
    static JPanel gameDetails;
    static JButton reset;

    // Multiplayer components
    static ArrayList<Cell> onlineSelected;
    static HashSet<String> onlineWordSet;
    static JPanel scoreArea;
    static JTextArea scores;
    static JScrollPane leaderBoard;
    static JPanel onlineBtns;
    static JButton leave;
    static JButton host;
    static JButton connect;
    static JPanel onlineGameArea;
    static JButton [] onlineBlocks;
    static Server server;
    static Client client;
    static JButton onlineStart;
    static JLabel timeLabel;
    static int timeLeft = 60;
    static boolean start= false;
    static Timer timer;
    public static void createAndShowGUI(){
        bg = new BoggleGame();
        wordSet = bg.findAllGameWords(dictionary);

        game = new JFrame("Boggle");
        game.setPreferredSize(new DimensionUIResource(500,500));
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainArea = new JTabbedPane();
        singlePlayer = new JPanel();
        multiplayer = new JPanel(new GridLayout(2,1));
        multiplayer.setBackground(Color.gray);

        mainArea.addTab("Single Player", singlePlayer);
        singlePlayer.setLayout(new GridLayout(2,2,3,3));
        singlePlayer.setBackground(Color.gray);
        initSinglePlayer();

        mainArea.addTab("Multi-Player", multiplayer);
        initMultiplayer();


        game.add(mainArea);
        game.pack();
        game.setVisible(true);
    }

    public static void initMultiplayer(){
        onlineSelected = new ArrayList<>();
        onlineWordSet = new HashSet<>();

        scoreArea = new JPanel(new BorderLayout());
        scoreArea.setBackground(Color.gray);

        timeLabel = new JLabel ("Time: " + timeLeft);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        timeLabel.setBackground(Color.gray);

        onlineBtns = new JPanel(new GridLayout(3,1));
        leave = new JButton("Leave");
        host = new JButton("Host...");
        connect = new JButton("Connect...");
        onlineStart = new JButton("Start");
        onlineStart.setEnabled(false);
        onlineStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                client.talkToServer("START");
                start = true;
                onlineGameArea.setVisible(true);
                onlineStart.setEnabled(true);
                onlineStart.setEnabled(false);
            }
        });

        // Host button creates a server to listen for clients
        host.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InetAddress ip;
                try {
                    ip = InetAddress.getLocalHost();
                } catch (UnknownHostException e1) {
//                    e1.printStackTrace();
                    return;
                }

                String port = JOptionPane.showInputDialog(multiplayer,"Your ip is: " + ip.getHostAddress() +"\nWhich port to listen on?");
                try {
                    server = new Server(Integer.parseInt(port), dictionary);
                    server.execute();
                } catch (NumberFormatException invalidSocket) {
                    JOptionPane.showMessageDialog(multiplayer, "Invalid socket number.");
                    return;
                } catch (Exception serverErr) {
                    JOptionPane.showMessageDialog(multiplayer, "Error creating server.");
                    server.closeServer();
                    return;
                }

                // Host also becomes a client
                String name = JOptionPane.showInputDialog(multiplayer, "Game username:");
                if (name.equals("") || port.equals("")) {
                    JOptionPane.showMessageDialog(multiplayer, "Could not create a connection");
                    return;
                }
                newClient(ip.getHostAddress(), Integer.parseInt(port), name);

                scores.setText("Leaderboard:");
                connect.setEnabled(false);
                host.setEnabled(false);
                onlineGameArea.setVisible(false);
                onlineStart.setEnabled(true);
            }
        });

        // Connect button creates a new client that will connect to the server
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scores.setText("Leaderboard:");
                newClient();
            }
        });

        // Leave button leaves the game
        leave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scores.setText("Leaderboard:");
                client.talkToServer("FINISHED");
                GUI.timer.cancel();
                gameEnd();
            }
        });

        scores = new JTextArea("Leaderboard:");
        scores.setBackground(Color.gray);
        scores.setEditable(false);
        scores.setLineWrap(true);

        leaderBoard = new JScrollPane(scores, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
        leaderBoard.setBackground(Color.gray);

        onlineBtns.add(host);
        onlineBtns.add(connect);
        onlineBtns.add(onlineStart);
        onlineBtns.add(leave);
        onlineBtns.add(timeLabel);
        onlineBtns.setBackground(Color.gray);

        scoreArea.setLayout(new BorderLayout());
        scoreArea.add(onlineBtns, BorderLayout.EAST);
        scoreArea.add(leaderBoard);
        scoreArea.setBackground(Color.gray);

        onlineGameArea = new JPanel(new GridLayout(4,4,3,3));
        onlineGameArea.setVisible(false);
        onlineGameArea.setBackground(Color.gray);

        multiplayer.add(scoreArea);
        multiplayer.add(onlineGameArea);
    }

    public static void initOnlineButtons(String board) {
        onlineGameArea.removeAll();

        onlineBlocks = new JButton[board.length()];
        for (int i = 0; i < board.length(); i++) {
            onlineBlocks[i] = new JButton(Character.toString(board.charAt(i)));
            onlineBlocks[i].setBackground(Color.gray);
            onlineBlocks[i].setOpaque(true);
            onlineBlocks[i].setName(Integer.toString(i));
            onlineBlocks[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    if (source instanceof JButton) {
                        if (((((JButton) source).getBackground()) != Color.gray) || (!onlineSelected.isEmpty() && !isBeside(4, onlineSelected.get(onlineSelected.size() - 1).index, Integer.parseInt(((JButton) source).getName())))) {
//                            System.out.println(selected.toString());

                            // Reset the selection if choose previously selected block or not beside the last selected one
                            onlineSelected.clear();
                            for (int i = 0; i < board.length(); i++) {
                                onlineBlocks[i].setBackground(Color.gray);
                            }
                            ((JButton) source).setBackground(Color.orange);

                        } else {
                            ((JButton) source).setBackground(Color.orange);
                        }
                        onlineSelected.add(new Cell(Integer.parseInt(((JButton) source).getName()), ((JButton) source).getText()));

                        // Check word
                        String word = "";
                        for (Cell block : onlineSelected) {
                            word += block.toString();
                        }

                        // Is the selection a match
                        if (onlineWordSet.contains(word)) {
                            client.talkToServer("UPDATE" + "," + word.length());
                            onlineWordSet.remove(word);
//                                remaining.setText("Remaining: " + Integer.toString(wordSet.size()));

                            // Clear the selections
                            onlineSelected.clear();
                            for (int i = 0; i < board.length(); i++) {
                                onlineBlocks[i].setBackground(Color.gray);
                            }

                            if (wordSet.isEmpty()) {
                                JOptionPane.showMessageDialog(multiplayer, "Congratulations, you won!");
                            }
                        }
                    }
                }
            });
            onlineGameArea.add(onlineBlocks[i]);
        }
    }


    public static void pollServer(){
        GUI.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                start = false;

                // Await for start signal
                try {
                    if (!start && client.talkToServer("CANSTART?").equals("START")) {
                        onlineGameArea.setVisible(true);
                        onlineStart.setEnabled(true);
                        onlineStart.setEnabled(false);
                        start = true;
                    }

                    // Update the leaderboard every second
                    scores.setText("Leaderboard:\n");
                    String serverScores = client.talkToServer("SCORES");
                    if (serverScores == null) {
                        gameEnd();
                    }
                    if (serverScores == "FINISHED") {
                        gameEnd();
                        timer.cancel();
                    }

                    String[] pairs = serverScores.split(",");
                    for (String pair : pairs) {
                        String[] user = pair.split(":");
                        if (user.length == 2)
                            scores.setText(scores.getText() + user[0] + ":" + user[1] + "\n");
                    }

                    // Update the time remaining
                    if (start) {
                        timeLeft--;
                        GUI.timeLabel.setText("Timer: " + timeLeft);
                        if (timeLeft == 0) {
                            gameEnd();
                            timer.cancel();
                        }
                    }
                } catch (Exception e) {
                    gameEnd();
                }
            }
        }, 1000, 1000);
    }

    public static void gameEnd(){
        JOptionPane.showMessageDialog(multiplayer, "Game has ended");
        GUI.timer.cancel();

        if (server != null) {
            server.closeServer();
        }

        if (client != null)
            client.closeSocket();

        connect.setEnabled(true);
        host.setEnabled(true);
        onlineStart.setEnabled(false);
        onlineGameArea.removeAll();
        timeLeft = 60;
        timeLabel.setText("Time: " + timeLeft);
        multiplayer.repaint();
    }

    public static void newClient (String hostName , int port, String name){
        client = new Client(hostName,  port, name);

        // Get the board
        String board = client.talkToServer("INIT");
        initOnlineButtons(board);

        // Get the word set
        onlineWordSet.clear();
        String wordSet = client.talkToServer("WORDSET");
        String[] words = wordSet.split("-");

        for (String word : words)
            onlineWordSet.add(word);

        pollServer();
    }

    public static void newClient (){
        String hostName = JOptionPane.showInputDialog(multiplayer,"Local ip (Hostname): ");
        try {
            int port = Integer.parseInt(JOptionPane.showInputDialog(multiplayer,"Port Number: "));
            String name = JOptionPane.showInputDialog(multiplayer,"What is your in-game name");
            client = new Client(hostName,  port, name);

            // Get the board
            String board = client.talkToServer("INIT");
            initOnlineButtons(board);

            // Get the word set
            onlineWordSet.clear();
            String wordSet = client.talkToServer("WORDSET");
            String[] words = wordSet.split("-");

            for (String word : words)
                onlineWordSet.add(word);
        } catch(NumberFormatException numErr) {
            JOptionPane.showMessageDialog(multiplayer, "Invalid port number");
        }

        pollServer();
    }

    public static void initSinglePlayer(){
        remaining = new JLabel("Remaining: " + Integer.toString(wordSet.size()), SwingConstants.CENTER);
        remaining.setFont(new Font("SansSerif", Font.BOLD, 25));
        remaining.setBackground(Color.gray);

        letterBtns = new JButton[bg.getBoardSize()];
        selected = new ArrayList<>(16);

        headerText = new JTextArea("Find as many words as you can by linking letters horzontally, vertically, or diagonally.\n" +
                                    "Words must be greater than 2 letters\n" +
                                    "Select a previously selected block or a gray block to reset your selection\n");
        headerText.setLineWrap(true);
        headerText.setEditable(false);
        headerText.setBackground(Color.gray);

        // Populate board with letters
        usrAnswers = new JTextArea();
        scrollAnswers = new JScrollPane (usrAnswers,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollAnswers.setBackground(Color.gray);
        scrollAnswers.setAutoscrolls(true);
        usrAnswers.setBackground(Color.gray);
        usrAnswers.setEditable(false);

        playSpace = new JPanel(new GridLayout(4,4,3,3));
        playSpace.setBackground(Color.gray);

        initBlocks();

        // Reset the game
        reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bg = new BoggleGame();
                selected.clear();
                wordSet = bg.findAllGameWords( dictionary);
                usrAnswers.setText("");
                playSpace.removeAll();
                remaining.setText("Remaining: " + Integer.toString(wordSet.size()));
                letterBtns = new JButton[bg.getBoardSize()];
                initBlocks();
                game.repaint();
            }
        });

        gameDetails = new JPanel();
        gameDetails.setLayout(new BorderLayout());
        gameDetails.setBackground(Color.gray);
        gameDetails.add(playSpace, BorderLayout.CENTER);
        gameDetails.add(remaining, BorderLayout.NORTH);
        gameDetails.add(reset, BorderLayout.EAST);

        header = new JPanel();
        header.setLayout(new GridLayout(1,2));
        header.setBackground(Color.gray);
        header.add(headerText);
        header.add(scrollAnswers);

        singlePlayer.add(header);
        singlePlayer.add(gameDetails);
    }

    public static void initBlocks(){
        // Initialize the GUI blocks
        String board = bg.toString();
        for (int i = 0; i < board.length(); i++) {
            letterBtns[i] = new JButton(Character.toString(board.charAt(i)));
            letterBtns[i].setBackground(Color.gray);
            letterBtns[i].setOpaque(true);
            letterBtns[i].setName(Integer.toString(i));
            letterBtns[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    if (source instanceof JButton) {
                        if ( ((((JButton) source).getBackground()) != Color.gray ) || (!selected.isEmpty() && !isBeside(4, selected.get(selected.size()-1).index, Integer.parseInt(((JButton) source).getName())))) {
//                            System.out.println(selected.toString());

                            // Reset the selection if choose previously selected block or not beside the last selected one
                            selected.clear();
                            for (int i = 0; i < board.length(); i++) {
                                letterBtns[i].setBackground(Color.gray);
                            }
                            ((JButton) source).setBackground(Color.orange);

                        } else {
                            ((JButton) source).setBackground(Color.orange);
                        }
                        selected.add(new Cell(Integer.parseInt(((JButton) source).getName()), ((JButton) source).getText()));
                        // Check word
                        String word = "";
                        for (Cell block: selected) {
                            word += block.toString();
                        }

                        // Is the selection a match
                        if (wordSet.contains(word)) {
                            usrAnswers.setText(usrAnswers.getText() + "\n" + word);
                            wordSet.remove(word);
                            remaining.setText("Remaining: " + Integer.toString(wordSet.size()));

                            // Clear the selections
                            selected.clear();
                            for (int i = 0; i < board.length(); i++) {
                                letterBtns[i].setBackground(Color.gray);
                            }

                            if (wordSet.isEmpty()) {
                                JOptionPane.showMessageDialog(singlePlayer, "Congratulations, you won!");
                            }
                        }
                    }
                }
            });
            playSpace.add(letterBtns[i]);
        }
    }

    // Checks 360 degrees around the block to see if toTest is around og
    public static boolean isBeside (int dimension, int og, int toTest){
        int difference = og / 4 - toTest/4;

        // Check that it's in row above, below, or same
        if (difference <= 1 && difference >= -1) {
            if (toTest % 4 <= (og % 4 + 1) && toTest %4 >= (og % 4 -1) ) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    createAndShowGUI();
                }
            });
        } catch (Exception e) {

        }

    }

}
