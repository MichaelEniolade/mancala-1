package me.dacol.marco.mancala.gameLib.board;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dac on 25/11/14.
 */
public class BoardManager implements Observer {

    BoardStatus boardStatus;

    public BoardManager(BoardStatus boardStatus) {
        this.boardStatus = boardStatus;
    }

    // Getter
    public BoardStatus getBoardStatus() {
        return boardStatus;
    }

    @Override
    public void update(Observable observable, Object data) {

    }
}
