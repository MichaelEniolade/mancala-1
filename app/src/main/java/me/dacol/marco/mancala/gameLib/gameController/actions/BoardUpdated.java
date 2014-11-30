package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

/***
 * Context action to indicate that the board has completed the update process,
 * the load of the action is a boolean field indicating if there is a winner in the
 * current status of the board
 */
public class BoardUpdated extends Action<ArrayList> {

    private boolean mIsGameEnded;
    private boolean mAnotherRound;

    public BoardUpdated(ArrayList load, boolean isGameEnded, boolean anotherRound) {
        super(load);
        mIsGameEnded = isGameEnded;
        mAnotherRound = anotherRound;
    }

    public boolean isGameEnded() {
        return mIsGameEnded;
    }

    public boolean isAnotherRound() {
        return mAnotherRound;
    }
}
