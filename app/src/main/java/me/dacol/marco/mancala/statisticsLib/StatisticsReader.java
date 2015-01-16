package me.dacol.marco.mancala.statisticsLib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import me.dacol.marco.mancala.statisticsLib.DBContracts.StatsHvCEntry;
import me.dacol.marco.mancala.statisticsLib.DBContracts.StatsHvHEntry;
import me.dacol.marco.mancala.statisticsLib.DBContracts.GamesHistoryEntry;

public class StatisticsReader extends AsyncTask<Void, Void, List<Cursor>> {

    private Context mContext;
    private StatisticsCallerObject mCallerObject;

    public StatisticsReader(Context context, StatisticsCallerObject callerObject) {
        mContext = context;
        mCallerObject = callerObject;
    }

    @Override
    protected List<Cursor> doInBackground(Void... params) {
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        DBHelper statsDBHelper = new DBHelper(mContext);
        SQLiteDatabase db = statsDBHelper.getReadableDatabase();

        // query for HvC statistics
        Cursor HvCStatistics = db.query(
                StatsHvCEntry.TABLE_NAME,
                new String[]{StatsHvCEntry.COLUMN_NAME_KEY, StatsHvCEntry.COLUMN_NAME_VALUE},
                null, null, null, null, null);

        // query for HVH statistics
        Cursor HvHStatistics = db.query(
                StatsHvHEntry.TABLE_NAME,
                new String[]{StatsHvHEntry.COLUMN_NAME_KEY, StatsHvHEntry.COLUMN_NAME_VALUE},
                null, null, null, null, null);

        // query for all the last games played
        Cursor gamesHistory = db.query(
                GamesHistoryEntry.TABLE_NAME,
                new String[] { GamesHistoryEntry._ID,
                               GamesHistoryEntry.COLUMN_NAME_DATE,
                               GamesHistoryEntry.COLUMN_NAME_PLAYER_POINT,
                               GamesHistoryEntry.COLUMN_NAME_OPPONENT_POINT,
                               GamesHistoryEntry.COLUMN_NAME_GAME_TYPE },
                null,
                null,
                null,
                null,
                GamesHistoryEntry._ID + " DESC",
                "10"
        );

        // put all the cursor in a List and return that
        List<Cursor> cursors = new ArrayList<>();
        cursors.add(HvCStatistics);
        cursors.add(HvHStatistics);
        cursors.add(gamesHistory);

        return cursors;
    }

    @Override
    protected void onPostExecute(List<Cursor> cursors) {
        super.onPostExecute(cursors);
        mCallerObject.postResults(cursors);
    }
}
