package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public abstract class Container {

    int numberOfSeeds = 0;
    Player owner = null;

    public Container(Player owner) {
        this.owner = owner;
    }

    //Getters
    public int getNumberOfSeeds() {
        return numberOfSeeds;
    }

    public Player getOwner() {
        return owner;
    }
}
