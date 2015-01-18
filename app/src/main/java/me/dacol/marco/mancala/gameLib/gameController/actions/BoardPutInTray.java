package me.dacol.marco.mancala.gameLib.gameController.actions;

/**
 * Action used to represent the increment of seeds in a players tray
 */
public class BoardPutInTray extends Action<Integer> {

    private int mNumberOfSeedsToPut;
    private int mNumberOfSeeds;
    /**
     * The load represent the integer position of the tray in which the seeds are going to be added
     * since the seeds can be multiple in one time, there is an additional argument to set the
     * precise number of seeds
     * @param load of the container
     * @param numberOfSeedsToPut to put in the container
     */
    public BoardPutInTray(Integer load, int numberOfSeedsToPut, int numberOfSeeds) {
        super(load);
        mNumberOfSeedsToPut = numberOfSeedsToPut;
        mNumberOfSeeds = numberOfSeeds;
    }

    /**
     * Return the number of seed to put in the tray
     * @return the number of seed added in the tray
     */
    public int getNumberOfSeedsToPut() {
        return mNumberOfSeedsToPut;
    }

    /**
     * Return the number of seeds after the move
     * @return int number of seeds
     */
    public int getNumberOfSeeds() {
        return mNumberOfSeeds;
    }
}
