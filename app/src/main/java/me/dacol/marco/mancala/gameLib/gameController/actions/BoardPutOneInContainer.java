package me.dacol.marco.mancala.gameLib.gameController.actions;

/**
 * Action used to represent the moves of putting one seed in one bowl
 */
public class BoardPutOneInContainer extends Action<Integer> {

    private int mNumberOfSeeds;

    /**
     * The load here is the integer position number of the bowl in which is going to be added a seed
     * @param load
     */
    public BoardPutOneInContainer(Integer load, int numberOfSeeds) {
        super(load);
        mNumberOfSeeds = numberOfSeeds;
    }

    /**
     * Return the number of seeds after the move
     * @return int number of seeds
     */
    public int getNumberOfSeeds() {
        return mNumberOfSeeds;
    }
}
