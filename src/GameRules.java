/**
 * Author: Benjamin Baird
 * Created on: 2016-11-18
 * Last Updated on: 2016-11-18
 * Filename: GameRules
 * Description: The rules of the boggle game.
 */
public class GameRules {
    private int boardSize;
    private int timeLimit;
    private boolean hints ;

    public GameRules(){
        boardSize = 4;
        timeLimit = 60;
        hints = false;
    }

    public GameRules (int boardSize, int timeLimit, boolean hints) {
        this.boardSize = boardSize;
        this.timeLimit = timeLimit;
        this.hints = hints;
    }

    public boolean getHintsEnabled(){
        return hints;
    }

    public int getBoardSize(){
        return boardSize;
    }

    public int getTimeLimit(){
        return timeLimit;
    }
}
