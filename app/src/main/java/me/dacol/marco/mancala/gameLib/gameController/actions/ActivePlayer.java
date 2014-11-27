package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;

public class ActivePlayer extends Action<Player> {

    private ArrayList<Container> mBoardRepresentation;

    public ActivePlayer(Player load, ArrayList<Container> boardRepresentation) {
        super(load);
        mBoardRepresentation = boardRepresentation;
    }

    public ArrayList<Container> getBoardRepresentation() {
        return mBoardRepresentation;
    }
}
