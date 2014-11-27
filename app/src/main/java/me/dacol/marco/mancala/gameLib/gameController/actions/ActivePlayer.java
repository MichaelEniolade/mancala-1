package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;

public class ActivePlayer extends Action<Player> {

    private ArrayList<Container> mBoardRepresentation;
    private int mLastOpponentMove;

    public ActivePlayer(Player load, ArrayList<Container> boardRepresentation, int lastOpponentMove) {
        super(load);
        mBoardRepresentation = boardRepresentation;
        mLastOpponentMove = lastOpponentMove;
    }

    public ArrayList<Container> getBoardRepresentation() {
        return mBoardRepresentation;
    }

    public int getLastOpponentMove() {
        return mLastOpponentMove;
    }
}
