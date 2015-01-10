package me.dacol.marco.mancala.gameLib.gameController.actions;

/**
 * Action used to represent the moves of putting one seed in one bowl
 */
public class BoardPutOneInContainer extends Action<Integer> {

    /**
     * The load here is the integer position number of the bowl in which is going to be added a seed
     * @param load
     */
    public BoardPutOneInContainer(Integer load) {
        super(load);
    }
}
