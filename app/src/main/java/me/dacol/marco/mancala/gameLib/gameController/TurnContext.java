package me.dacol.marco.mancala.gameLib.gameController;

import java.util.Observable;
import java.util.Stack;

import me.dacol.marco.mancala.gameLib.gameController.actions.Action;

/***
 * Here is published in which moment of the game we are,
 * possible action are:
 * - which player has to play
 * - player have choose a bowl
 * - board updated or invalid move
 * - next turn or end game
 */
public class TurnContext extends Observable {
    // Right now is just a wrapper around a Stack object,
    // but these gives room from improvements like keep the game history
    // to allow the player to undo moves.
    // It can also improve statistics.
    private Stack<Action> mActionList = new Stack<Action>();

    // Singleton
    private static TurnContext sInstance = null;
    protected TurnContext() {}
    public static TurnContext getInstance() {
        if (sInstance == null) {
            sInstance = new TurnContext();
        }
        return sInstance;
    }

    // Reinitialize the Action list at each turn in case it's not empty
    public void initialize() {
        if (!mActionList.empty()) {
            do {
                mActionList.pop();
            } while (!mActionList.empty());
        }
    }

    public void push(Action action) {
        mActionList.push(action);
        setChanged();
        notifyObservers(action); // i pass the last pushed object
    }

    public Action pop() {
        return mActionList.pop();
    }

    public Action peek() {
        return mActionList.peek();
    }
}
