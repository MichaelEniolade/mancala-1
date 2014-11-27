package me.dacol.marco.mancala.gameLib.board;

import me.dacol.marco.mancala.gameLib.player.Player;

public class Bowl extends Container {

    public Bowl(Player owner) {
        super(owner);
        this.numberOfSeeds = 3; //starting # of seeds in any bowl
    }

    // In any bowl i can add only one seed at the time
    public void putSeed() {
        this.numberOfSeeds += 1;
    }

    // Grab all the seeds contained in a bowl
    public int emptyBowl() {
        int seeds = this.numberOfSeeds;
        this.numberOfSeeds = 0;

        return seeds;
    }

}
