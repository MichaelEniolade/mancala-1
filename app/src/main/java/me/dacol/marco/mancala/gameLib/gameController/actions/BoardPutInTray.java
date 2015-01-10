package me.dacol.marco.mancala.gameLib.gameController.actions;

/**
 * Action used to represent the increment of seeds in a players tray
 */
public class BoardPutInTray extends Action<Integer> {

    private int mNumberOfSeeds;

    /**
     * The load represent the integer position of the tray in which the seeds are going to be added
     * since the seeds can be multiple in one time, there is an additional argument to set the
     * precise number of seeds
     * @param load
     * @param numberOfSeeds
     */
    public BoardPutInTray(Integer load, int numberOfSeeds) {
        super(load);
        mNumberOfSeeds = numberOfSeeds;
    }

    public int getNumberOfSeeds() {
        return mNumberOfSeeds;
    }
}
