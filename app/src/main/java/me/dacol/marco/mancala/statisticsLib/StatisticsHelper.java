package me.dacol.marco.mancala.statisticsLib;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;

/**
 * This class is a singleton, you don't need more than one StatisticsHelper around.
 * To use the method LoadAllStatistics it is necessary to have a callback object implementing
 * StatisticsCallerObject, otherwise the result of the call will be lost
 */
public class StatisticsHelper implements Observer {
    private static final String LOG_TAG = StatisticsHelper.class.getSimpleName();

    private static StatisticsHelper mInstance;

    private Date mDate;
    private Context mContext;

    public static StatisticsHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StatisticsHelper(context);
        }

        return mInstance;
    }

    private StatisticsHelper(Context context) {
        mDate = new Date();
        mContext = context;
    }

    /**
     * Called when the game is ended, launch an AsyncTask that update the game statistics database
     * @param boardRepresentation the boardRepresentation from the game
     */
    public void register(ArrayList<Container> boardRepresentation) {
        new StatisticsRegister(mContext, mDate).execute(boardRepresentation);
    }

    /**
     * Called to retrieve the statistics from the database, fires an AsyncTask that does the queries
     * @param callerObject this is usually the calling object that implements the interface for the callback
     */
    public void loadAllStatistics(StatisticsCallerObject callerObject) {
        new StatisticsReader(mContext, callerObject).execute();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Winner) {
            register(((Winner) data).getboardStatus());
        } else if (data instanceof EvenGame) {
            register(((EvenGame) data ).getLoad());
        }
    }
}