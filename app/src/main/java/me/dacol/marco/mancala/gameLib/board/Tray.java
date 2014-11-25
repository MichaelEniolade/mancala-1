package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

/**
 * Created by Dac on 25/11/14.
 */
public class Tray extends Container{

    public Tray(Player owner) {
        super(owner);
    }

    public void putSeeds(int quantity) {
        this.numberOfSeeds += quantity;
    }

}
