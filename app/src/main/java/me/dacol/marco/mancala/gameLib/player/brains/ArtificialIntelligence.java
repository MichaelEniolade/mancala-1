package me.dacol.marco.mancala.gameLib.player.brains;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.board.Move;
import me.dacol.marco.mancala.gameLib.player.Player;

public class ArtificialIntelligence extends BaseBrain {

    public ArtificialIntelligence(Player player, int numberOfBowl, int numberOfTray) {
        super(player, numberOfBowl, numberOfTray);
    }

    @Override
    public Move makeMove(ArrayList<Container> boardStatus) {
        return super.makeMove(boardStatus);
    }
}
