package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public abstract class Container {

    protected int mNumberOfSeeds = 0;
    protected Player mOwner = null;

    public Container(Player owner) {
        mOwner = owner;
    }

    //Getters
    public int getNumberOfSeeds() {
        return mNumberOfSeeds;
    }

    public Player getOwner() {
        return mOwner;
    }

    // Setters
    public void putOneSeed() {
        mNumberOfSeeds += 1;
    }
}
