package me.dacol.marco.mancala.gameLib.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.player.Player;

public class Board implements Observer {

    List<Bowl> mBowls = new ArrayList<Bowl>();
    List<Tray> mTrays = new ArrayList<Tray>();

    // Singleton
    private static Board sInstance = null;

    protected Board() {}

    public static Board getInstance() {
        if (sInstance == null) {
            sInstance = new Board();
        }

        return sInstance;
    }

    public void initialSetup(int numberOfPlayers) {
        // reset the board from any previous game

        // setup the board

    }





    @Override
    public void update(Observable observable, Object data) {

    }

    public boolean checkForWinner() {
        //check if there is a winner in the current board siatuation
        return false;
    }

    public Player getWinner() {
        return null;
    }
}
