package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public class Tray extends Container{

    public Tray(Player owner) {
        super(owner);
    }

    public void putSeeds(int quantity) {
        this.numberOfSeeds += quantity;
    }

}
