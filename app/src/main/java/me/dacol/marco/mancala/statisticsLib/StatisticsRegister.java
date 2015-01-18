package me.dacol.marco.mancala.statisticsLib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.dacol.marco.mancala.gameLib.board.Container;
import me.dacol.marco.mancala.statisticsLib.DBContracts.GamesHistoryEntry;
import me.dacol.marco.mancala.statisticsLib.DBContracts.StatsHvCEntry;
import me.dacol.marco.mancala.statisticsLib.DBContracts.StatsHvHEntry;
/**
 * This class has the role of registering all the game statistics during a game
 * Listen to the TurnContext to intercept the game event and responds to them
 */
public class StatisticsRegister extends AsyncTask<ArrayList<Container>, Void, Void> {
    private static final String LOG_TAG = StatisticsRegister.class.getSimpleName();

    private Date mStartedGame;
    private DBHelper mDBHelper;

    public StatisticsRegister(Context context, Date startedGame) {
        mStartedGame = startedGame;
        mDBHelper = new DBHelper(context);
    }

    @Override
    protected Void doInBackground(ArrayList<Container>... params) {
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        ArrayList<Container> boardRepresentation = params[0];

        // - first calculate the game data
        int playerScore = boardRepresentation.get(6).getNumberOfSeeds();
        int opponentScore = boardRepresentation.get(13).getNumberOfSeeds();
        String gameType = (boardRepresentation.get(12).getOwner().isHuman()) ?
                DBContracts.GAME_TYPE_HvH : DBContracts.GAME_TYPE_HvC;

        // - retrieve the stored data
        Map<String, Integer> statsMap = loadKeyValueStatisticsFromDatabase(gameType);

        // - update the data where necessary
        // increment playedGames
        statsMap.put(DBContracts.STATS_KEY_PLAYED_GAME,
                statsMap.get(DBContracts.STATS_KEY_PLAYED_GAME) + 1);

        // increment the result value
        if (playerScore > opponentScore)
            statsMap.put(DBContracts.STATS_KEY_WIN_GAME,
                    statsMap.get(DBContracts.STATS_KEY_WIN_GAME) + 1);
        else if (playerScore < opponentScore)
            statsMap.put(DBContracts.STATS_KEY_LOSE_GAME,
                    statsMap.get(DBContracts.STATS_KEY_LOSE_GAME) + 1);
        else if (playerScore == opponentScore)
            statsMap.put(DBContracts.STATS_KEY_EVEN_GAME,
                    statsMap.get(DBContracts.STATS_KEY_EVEN_GAME) + 1);

        if (playerScore > statsMap.get(DBContracts.STATS_KEY_BESTSCORE))
            statsMap.put(DBContracts.STATS_KEY_BESTSCORE, playerScore);

        // - Create the set of data for the game history table
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        ContentValues gamesHistoryContentValue = new ContentValues();
        gamesHistoryContentValue.put(GamesHistoryEntry.COLUMN_NAME_DATE, df.format(mStartedGame));
        gamesHistoryContentValue.put(GamesHistoryEntry.COLUMN_NAME_PLAYER_POINT, playerScore);
        gamesHistoryContentValue.put(GamesHistoryEntry.COLUMN_NAME_OPPONENT_POINT, opponentScore);
        gamesHistoryContentValue.put(GamesHistoryEntry.COLUMN_NAME_GAME_TYPE, gameType);

        // - persist all the data in the DB
        persistInDatabase(gamesHistoryContentValue, statsMap, gameType);

        return null;
    }

    private Map<String, Integer> loadKeyValueStatisticsFromDatabase(String gameType) {
        String[] projection = new String[]{ StatsHvCEntry.COLUMN_NAME_KEY, StatsHvCEntry.COLUMN_NAME_VALUE };
        String table = StatsHvCEntry.TABLE_NAME;

        if (gameType.equals( DBContracts.GAME_TYPE_HvH )) {
            projection = new String[]{ StatsHvHEntry.COLUMN_NAME_KEY, StatsHvHEntry.COLUMN_NAME_VALUE };
            table = StatsHvHEntry.TABLE_NAME;
        }

        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.query(table, projection, null, null, null, null, null);

        Map<String, Integer> dbStatsMap = new HashMap<>();

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbStatsMap.put(cursor.getString(0), cursor.getInt(1));
            }
        }

        return dbStatsMap;
    }

    private void persistInDatabase(ContentValues gamesHistoryContentValue,
                                   Map<String, Integer> statsMap,
                                   String gameType) {

        String table = StatsHvCEntry.TABLE_NAME;
        String keyColumnName = StatsHvCEntry.COLUMN_NAME_KEY;
        String valueColumnName = StatsHvCEntry.COLUMN_NAME_VALUE;

        if (gameType.equals(DBContracts.GAME_TYPE_HvH)) {
            table = StatsHvHEntry.TABLE_NAME;
            keyColumnName = StatsHvHEntry.COLUMN_NAME_KEY;
            valueColumnName = StatsHvHEntry.COLUMN_NAME_VALUE;
        }

        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // update the statistics, it is a bit more complicated, I've to iterate through the hashMap
        for (Map.Entry<String, Integer> statistic : statsMap.entrySet()) {
            ContentValues entry = new ContentValues();
            entry.put(valueColumnName, statistic.getValue());
            db.update(table, entry, keyColumnName + " =?", new String[]{statistic.getKey()});
        }
        // persist last game information
        db.insert(GamesHistoryEntry.TABLE_NAME, null, gamesHistoryContentValue);

    }

}