package me.dacol.marco.mancala.gameLib.gameController.actions;

/**
 * This action is used to represent steps-move on the board pieces
 */
public class BoardEmptyBowl extends Action<Integer> {

    private boolean mIsOppositeBowl;
    private int mNumberOfSeeds;
    /**
     * The load here is the integer position of the bowl that is going to be emptied
     * and a flag to indicate when this action is fired because of a stealing action
     * @param load
     */
    public BoardEmptyBowl(Integer load, boolean isOppositeBowl, int numberOfSeed) {
        super(load);
        mIsOppositeBowl = isOppositeBowl();
        mNumberOfSeeds = numberOfSeed;
    }

    public boolean isOppositeBowl() {
        return mIsOppositeBowl;
    }

    /**
     * Return the number of seeds after the move
     * @return int number of seeds
     */
    public int getNumberOfSeeds() {
        return mNumberOfSeeds;
    }
}
