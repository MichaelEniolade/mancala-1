package me.dacol.marco.mancala.gameLib.gameController.actions;

/***
 * Context action to indicate that the board has completed the update process,
 * the load of the action is a boolean field indicating if there is a winner in the
 * current status of the board
 */
public class BoardUpdated extends Action<Boolean> {

    protected BoardUpdated(Boolean load) {
        super(load);
    }

    public Boolean isGameEnded() {
        return getLoad();
    }


}
