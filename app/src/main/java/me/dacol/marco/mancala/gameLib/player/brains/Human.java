package me.dacol.marco.mancala.gameLib.player.brains;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.player.Player;

public class Human extends BaseBrain implements Brain {

    public Human(int numberOfBowl, int numberOfTray) {
        super(numberOfBowl, numberOfTray);
    }

    @Override
    public Move makeMove(ArrayList<Container> boardStatus, Player player) {
        return super.makeMove(boardStatus, player);
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
