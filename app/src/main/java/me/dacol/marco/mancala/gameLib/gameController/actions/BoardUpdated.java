package me.dacol.marco.mancala.gameLib.gameController.actions;

import java.util.ArrayList;

/***
 * Context action to indicate that the board has completed the update process,
 * the load of the action is an arrayList containing the current status of the board,
 * there is also two additional field, one if the player have the right to another round
 * and a second indicating all the atomic moves done to transition in the new board state
 */
public class BoardUpdated extends Action<ArrayList> {

    private boolean mAnotherRound;
    private ArrayList<Action> mAtomicMoves;

    public BoardUpdated(ArrayList load, boolean anotherRound, ArrayList atomicMoves) {
        super(load);
        mAnotherRound = anotherRound;
        mAtomicMoves = atomicMoves;
    }

    /***
     * Return a boolean indicating where a player has to play another round or not
     * @return boolean, true if the last seed was added to the player tray
     */
    public boolean isAnotherRound() {
        return mAnotherRound;
    }

    /**
     * Get the atomicMoves arraylist, which contains all the single moves done on the board update
     * @return ArrayList with all the moves made to transition the board from the previous state to the actual
     */
    public ArrayList<Action> getAtomicMoves() {
        return mAtomicMoves;
    }
}
