package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Winner extends Action<Player> {

    private ArrayList<Container> mBoardStatus;
    private ArrayList<Action> mAtomicMoves;

    public Winner(Player load, ArrayList<Container> boardStatus, ArrayList<Action> atomicMoves) {
        super(load);
        mBoardStatus = boardStatus;
        mAtomicMoves = atomicMoves;
    }

    public ArrayList<Container> getboardStatus() {
        return mBoardStatus;
    }

    public ArrayList<Action> getAtomicMoves() {
        return mAtomicMoves;
    }
}
