package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public class Tray extends Container{

    public Tray(Player owner) {
        super(owner, 0);
    }

    public Tray(Player owner, int numberOfSeeds) {
        super(owner, numberOfSeeds);
    }

    public void putSeeds(int quantity) {
        mNumberOfSeeds += quantity;
    }

}
