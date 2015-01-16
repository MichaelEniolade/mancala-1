package me.dacol.marco.mancala.statisticsLib;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.gameLib.gameController.actions.EvenGame;
import me.dacol.marco.mancala.gameLib.gameController.actions.Winner;
import me.dacol.marco.mancala.logging.Logger;

public class StatisticsHelper implements Observer {
    private static final String LOG_TAG = StatisticsHelper.class.getSimpleName();

    private Date mDate;
    private Context mContext;
    private StatisticsRegister mStatisticsRegister;

    public StatisticsHelper(Context context) {
        mDate = new Date();
        mContext = context;
        mStatisticsRegister = new StatisticsRegister(mContext, mDate);
    }

    public void register(ArrayList<Container> boardRepresentation) {
        mStatisticsRegister.execute(boardRepresentation);
    }

    public void getLoadAll(StatisticsCallerObject callerObject) {
        new StatisticsReader(mContext, callerObject).execute();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Winner) {
            Logger.v(LOG_TAG, "Winner, registro i dati");
            register(((Winner) data).getboardStatus());
        } else if (data instanceof EvenGame) {
            register(((EvenGame) data ).getLoad());
        }
    }
}
