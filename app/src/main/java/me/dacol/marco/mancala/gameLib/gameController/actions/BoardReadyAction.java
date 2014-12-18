package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;

public class BoardReadyAction extends Action<ArrayList<Container>> {
    public BoardReadyAction(ArrayList<Container> boardStatus) {
        super(boardStatus);
    }
}
