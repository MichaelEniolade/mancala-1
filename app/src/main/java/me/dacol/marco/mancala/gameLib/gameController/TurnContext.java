package me.dacol.marco.mancala.gameLib.gameController;

import java.util.ArrayList;
import java.util.Observable;

import me.dacol.marco.mancala.gameLib.gameController.actions.Action;
import me.dacol.marco.mancala.gameLib.gameController.actions.ActivePlayer;
import me.dacol.marco.mancala.gameLib.gameController.actions.BoardUpdated;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.InvalidMove;
import me.dacol.marco.mancala.gameLib.gameController.actions.MoveAction;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.logging.Logger;

/***
 * Here is published in which moment of the game we are,
 * possible action are:
 * - which player has to play
 * - player have choose a bowl_selected
 * - board updated or invalid move
 * - next turn or end game
 */
public class TurnContext extends Observable {
    private final static String LOG_TAG = TurnContext.class.getSimpleName();
    // Right now is just a wrapper around a Stack object,
    // but these gives room from improvements like keep the game history
    // to allow the player to undo moves.
    // It can also improve statistics.
    private ArrayList<Action> mActionList = new ArrayList<Action>();

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
        mActionList.clear();
        //if (!mActionList.empty()) {
        //    do {
        //        mActionList.pop();
        //    } while (!mActionList.empty());
        //}
    }

    public void push(Action action) {
        mActionList.add(action);

        log(action, true);

        setChanged();
        notifyObservers(action); // i pass the last pushed object
    }

    private void log(Action action, boolean isActive) {
        if (isActive) {
            if (action instanceof ActivePlayer) {
                Logger.v(LOG_TAG, "Actve Player: " + ((ActivePlayer) action).getLoad().getName());
            } else if (action instanceof BoardUpdated) {
                Logger.v(LOG_TAG, "Board Updated");
            } else if (action instanceof EvenGame) {
                Logger.v(LOG_TAG, "Even Game");
            } else if (action instanceof InvalidMove) {
                Logger.v(LOG_TAG, "Invalid Move of Player: " + ((InvalidMove) action).getPlayer().getName());
            } else if (action instanceof MoveAction) {
                Logger.v(LOG_TAG, "Move Action of PLayer: " + ((MoveAction) action).getLoad().getPlayer().getName());
            } else if (action instanceof Winner) {
                Logger.v(LOG_TAG, "Winner: " + ((Winner) action).getLoad().getName());
            }
        }
    }

    public Action pop() {
        Action action = mActionList.get(0);
        mActionList.remove(0);
        return action;
    }

    public Action peek() {
        return mActionList.get(0);
    }

    public int size() {
        return mActionList.size();
    }

    public Action getElement(int i) {
        return mActionList.get(i);
    }
}
