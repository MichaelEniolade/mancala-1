package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;

public class InvalidMove extends Action<Player> {

    private ArrayList<Container> mBoardStatus;
    private int mLastMove;

    public InvalidMove(Player load, ArrayList<Container> boardStatus, int lastMove) {
        super(load);
        mBoardStatus = boardStatus;
        mLastMove = lastMove;
    }

    public ArrayList<Container> getBoardStatus() {
        return mBoardStatus;
    }

    public int getLastMove() {
        return mLastMove;
    }
}
