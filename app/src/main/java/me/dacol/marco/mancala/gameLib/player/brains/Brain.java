package me.dacol.marco.mancala.gameLib.player.brains;

import java.util.ArrayList;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.player.Player;

public interface Brain {

    public void makeMove(ArrayList<Container> boardStatus, Player player);
    public void toggleLastMoveCameUpInvalid();
    public boolean isHuman();
    public void attachPlayer(AttachedPlayer attachedPlayer);

}
