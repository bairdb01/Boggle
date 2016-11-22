import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-18
 * Last Updated on: 2016-11-18
 * Filename: BoggleGame
 * Description: The boggle game implementation.
 */

public class BoggleGame {
    private String [][] board;
//            = {   {"T", "H", "E","M"},
//                                    {"Z","Z","Z", "Z"},
//                                    {"Z","Z","Z","Z"},
//                                    {"Z","Z","Z","Z"}};
    private GameRules rules;
    private String[] alphabet = { "A", "B", "C", "D", "E",
                                "F", "G", "H", "I", "J",
                                "K", "L", "M", "N", "O",
                                "P", "Q", "R", "S", "T",
                                "U", "V", "W", "X", "Y",
                                "Z"
                              };
    private double[] letterProbs = {     0.08167, 0.01492, 0.02782, 0.04253,
                                            0.12703, 0.02228, 0.02015, 0.06094,
                                            0.06966, 0.00153, 0.00772, 0.04025,
                                            0.02406, 0.06749, 0.07507, 0.01929,
                                            0.00095, 0.05987, 0.06327, 0.09056,
                                            0.02758, 0.00978, 0.02360, 0.00150,
                                            0.01974, 0.00074
                                        };

    public BoggleGame (String boardString) {
        this.rules = new GameRules();
        int boardSize = rules.getBoardSize();
        this.board = new String[boardSize][boardSize];
        if ( boardString.length() == boardSize*boardSize) {
            int counter = 0;
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    this.board[i][j] = Character.toString(boardString.charAt(counter));
                    counter++;
                }
            }
        }
    }

    public BoggleGame(){
        this.rules = new GameRules();
        // Initialize the letters
        int boardSize = rules.getBoardSize();
        this.board = new String[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                this.board[i][j] = genLetter();
            }
        }
    }

    public BoggleGame(GameRules rules){
        this.rules = rules;
        // Initialize the letter hashmap
        int boardSize = rules.getBoardSize();
        this.board = new String[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                this.board[i][j] = genLetter();
            }
        }
    }

    public String genLetter(){
        Random rn = new Random();
        double randNum = 0 + 1 - rn.nextDouble();
        double sum = 0;
        for (int i = 0; i < 26; i++) {
            sum += letterProbs[i];
            if (randNum <= sum) {
//                if (alphabet[i] == "Q")
//                    return "Qu";
                return alphabet[i];
            }
        }
        return " ";
    }

    public int getBoardDimen(){
        return board.length;
    }

    public String getBlock(int row, int col){
        return board[row][col];
    }

    public int getBoardSize(){
        return board.length*board.length;
    }

    public String toString(){
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board.length; j++)
                boardString.append(board[i][j]);

        return boardString.toString();
    }

    public HashSet<String> findAllGameWords (WordTrie dictionary) {
        BoggleGame game = this;
        String word = "";
        boolean [][] visited = new boolean[game.getBoardDimen()][game.getBoardDimen()];
        HashSet<String> boardWords = new HashSet<>();

        for (int row = 0; row < game.getBoardDimen(); row ++) {
            for (int col = 0; col < game.getBoardDimen(); col++) {
                boardWords.addAll(findBoardWords(game, dictionary, visited, row, col, word));
            }
        }
        return boardWords;
    }

    // Finds all the words in the current game
    private HashSet<String> findBoardWords(BoggleGame game, WordTrie dictionary, boolean [][] visited, int curRow, int curCol, String word) {
        HashSet<String> boardWords = new HashSet<>();
        visited[curRow][curCol] = true;

        word += game.getBlock(curRow, curCol);

        // Check if the word is a match
        if (dictionary.trieContains(word)) {
            boardWords.add(word);
        }

        // Look around the current block for potential characters to concatenate to the word
        for (int row = curRow-1; row <= (curRow+1) && row < game.getBoardDimen(); row++) {
            for (int col = curCol-1; col <= (curCol+1) && col < game.getBoardDimen(); col++) {
                if ( row < 0 || col < 0 || visited[row][col]) {
                    continue;
                }
                boardWords.addAll(findBoardWords(game, dictionary, visited, row, col, word));
            }
        }

        // Remove current letter from the prefix/word
        visited[curRow][curCol] = false;
        word.substring(0, word.length() - 1);

        return boardWords;
    }

}
