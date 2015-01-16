package me.dacol.marco.mancala.statisticsLib;

import android.provider.BaseColumns;

public final class DBContracts {

    public static final String GAME_TYPE_HvH = "HvH";
    public static final String GAME_TYPE_HvC = "HvC";

    // statistics key, are always the same that's why they are defined here
    public static final String STATS_KEY_BESTSCORE = "best_score";
    public static final String STATS_KEY_PLAYED_GAME = "played_games";
    public static final String STATS_KEY_WIN_GAME = "win_games";
    public static final String STATS_KEY_LOSE_GAME = "lose_games";
    public static final String STATS_KEY_EVEN_GAME = "even_games";

    public DBContracts() {
    }

    public static abstract class GamesHistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "games_history";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_PLAYER_POINT = "player_point";
        public static final String COLUMN_NAME_OPPONENT_POINT = "opponent_point";
        public static final String COLUMN_NAME_GAME_TYPE = "game_type";

    }

    public static abstract class StatsHvHEntry implements BaseColumns {
        public static final String TABLE_NAME = "stast_HvH";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    public static abstract class StatsHvCEntry implements BaseColumns {
        public static final String TABLE_NAME = "stast_HvC";
        public static final String COLUMN_NAME_KEY = "key";
        public static final String COLUMN_NAME_VALUE = "value";
    }

}
