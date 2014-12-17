package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Winner extends Action<Player> {

    private ArrayList<Container> mBoardStatus;

    public Winner(Player load, ArrayList<Container> boardStatus) {
        super(load);
        mBoardStatus = boardStatus;
    }

    public ArrayList<Container> getboardStatus() {
        return mBoardStatus;
    }
}
