package me.dacol.marco.mancala.statistics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import me.dacol.marco.mancala.statistics.DBContracts.*;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MancalaGame.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LAST_GAMES_HISTORY_TABLE = "" +
                "CREATE TABLE " + GamesHistoryEntry.TABLE_NAME + " (" +
                    GamesHistoryEntry._ID + " INTEGER PRIMARY KEY," +
                    GamesHistoryEntry.COLUMN_NAME_DATE + " TEXT NOT NULL," +
                    GamesHistoryEntry.COLUMN_NAME_PLAYER_POINT + " INTEGER NOT NULL," +
                    GamesHistoryEntry.COLUMN_NAME_OPPONENT_POINT + " INTEGER NOT NULL," +
                    GamesHistoryEntry.COLUMN_NAME_GAME_TYPE + " TEXT" +
                " );";

        final String SQL_CREATE_STATS_HVH_TABLE = "" +
                "CREATE TABLE " + StatsHvHEntry.TABLE_NAME + " (" +
                    StatsHvHEntry._ID + " INTEGER PRIMARY KEY," +
                    StatsHvHEntry.COLUMN_NAME_KEY + " TEXT NOT NULL," +
                    StatsHvHEntry.COLUMN_NAME_VALUE + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_STATS_HVC_TABLE = "" +
                "CREATE TABLE " + StatsHvCEntry.TABLE_NAME + " (" +
                    StatsHvCEntry._ID + " INTEGER PRIMARY KEY," +
                    StatsHvCEntry.COLUMN_NAME_KEY + " TEXT NOT NULL," +
                    StatsHvCEntry.COLUMN_NAME_VALUE + " INTEGER NOT NULL" +
                " );";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GamesHistoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StatsHvHEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StatsHvCEntry.TABLE_NAME);
        onCreate(db);
    }
}
