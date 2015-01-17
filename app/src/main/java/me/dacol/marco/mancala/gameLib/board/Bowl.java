package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public class Bowl extends Container {

    public Bowl(Player owner) {
        super(owner, 3);
    }

    public Bowl(Player owner, int numberOfSeeds) {
        super(owner, numberOfSeeds);
    }

    // Grab all the seeds contained in a bowl_selected
    public int emptyBowl() {
        int seeds = mNumberOfSeeds;
        mNumberOfSeeds = 0;

        return seeds;
    }

}
