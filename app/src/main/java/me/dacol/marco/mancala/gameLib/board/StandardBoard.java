package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.player.Player;

public interface StandardBoard<T> extends Observer {

    public void initialSetup(int numberOfPlayers);
    public ArrayList<T> getRepresentation();
    public Player getWinner();

}
