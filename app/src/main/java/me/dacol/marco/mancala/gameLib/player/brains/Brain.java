package me.dacol.marco.mancala.gameLib.player.brains;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;

public interface Brain {

    public Move makeMove(ArrayList<Container> boardStatus);
    public void toggleLastMoveCameUpInvalid();

 }
