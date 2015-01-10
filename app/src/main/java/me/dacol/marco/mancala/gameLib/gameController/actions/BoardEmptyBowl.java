package me.dacol.marco.mancala.gameLib.gameController.actions;

/**
 * This action is used to represent steps-move on the board pieces
 */
public class BoardEmptyBowl extends Action<Integer> {

    private boolean mIsOppositeBowl;
    /**
     * The load here is the integer position of the bowl that is going to be emptied
     * and a flag to indicate when this action is fired because of a stealing action
     * @param load
     */
    public BoardEmptyBowl(Integer load, boolean isOppositeBowl) {
        super(load);
        mIsOppositeBowl = isOppositeBowl();
    }

    public boolean isOppositeBowl() {
        return mIsOppositeBowl;
    }
}
