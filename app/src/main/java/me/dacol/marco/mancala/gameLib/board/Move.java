package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public class Move {

private int mBowlNumber;
private Player mPlayer;

    /***
     * Standard Move representation
     * @param bowlNumber the chosen bowl_selected
     * @param player the player that is making the move
     */
    public Move(int bowlNumber, Player player) {
        mBowlNumber = bowlNumber;
        mPlayer = player;
    }

    public int getBowlNumber() {
        return mBowlNumber;
    }

    public Player getPlayer() {
        return mPlayer;
    }
}