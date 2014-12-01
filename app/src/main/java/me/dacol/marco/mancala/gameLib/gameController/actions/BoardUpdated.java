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

    /***
     * Flag indicating if after the last move, the game is ended
     * @return boolean, true if one of the two player has ended the seeds in his bowl
     */
    public boolean isGameEnded() {
        return mIsGameEnded;
    }

    /***
     * Return a boolean indicating where a player has to play another round or not
     * @return boolean, true if the last seed was added to the player tray
     */
    public boolean isAnotherRound() {
        return mAnotherRound;
    }
}
