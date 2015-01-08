package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public class Bowl extends Container {

    public Bowl(Player owner) {
        super(owner);
        mNumberOfSeeds = 3; //starting # of seeds in any bowl_selected
    }

    // Grab all the seeds contained in a bowl_selected
    public int emptyBowl() {
        int seeds = mNumberOfSeeds;
        mNumberOfSeeds = 0;

        return seeds;
    }

}
