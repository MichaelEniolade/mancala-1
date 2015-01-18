package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.gameController.TurnContext;
import me.dacol.marco.mancala.gameLib.player.Player;

public interface StandardBoard<T> extends Observer {

    public Board setup(TurnContext turnContext, int numberOfBowl, int numberOfTray);
    public Board registerPlayers(List<Player> players);
    public void buildBoard(ArrayList<Integer> boardRepresentation);
    public ArrayList<T> getRepresentation();
    public Player getWinner();

}
