package me.dacol.marco.mancala.gameLib.gameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/***
 * Here is published in which moment of the game we are,
 * possible action are:
 * - which player has to play
 * - player have choose a bowl
 * - board updated or invalid move
 * - next turn or end game
 */
public class TurnContext extends Observable {
    // I know there is the Stack class in Java,
    // but in this way i can implement the undo action
    private List<Action> mActionList = new ArrayList<Action>();

    // Singleton
    private static TurnContext sInstance = null;
    protected TurnContext() {}
    public static TurnContext getInstance() {
        if (sInstance == null) {
            sInstance = new TurnContext();
        }
        return sInstance;
    }

    public void initialize() {
        // TODO write initialization for the TurnContext
    }

    public void post(Action action) {
        mActionList.add(action);
        setChanged();
        notifyObservers();
    }
}
