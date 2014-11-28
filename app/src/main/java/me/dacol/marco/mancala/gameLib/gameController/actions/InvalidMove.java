package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.player.Player;

public class InvalidMove extends Action<Move> {

    private ArrayList<Container> mBoardStatus;
    private Player mPlayer;

    public InvalidMove(Move load, ArrayList<Container> boardStatus, Player player) {
        super(load);
        mBoardStatus = boardStatus;
        mPlayer = player;
    }

    public ArrayList<Container> getBoardStatus() {
        return mBoardStatus;
    }

    public Player getPlayer() {
        return mPlayer;
    }
}
