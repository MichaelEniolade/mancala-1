package me.dacol.marco.mancala.logging;

import android.util.Log;

import me.dacol.marco.mancala.BuildConfig;

public class Logger {

    public static void v(String tag, String str) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, str);
        }
    }

    public static void d(String tag, String str) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, str);
        }
    }

    public static void i(String tag, String str) {
        if (Log.isLoggable(tag, Log.INFO)) {
            Log.i(tag, str);
        }
    }

    public static void e(String tag, String str) {
        if (Log.isLoggable(tag, Log.ERROR)) {
            Log.e(tag, str);
        }
    }

    public static void w(String tag, String str) {
        if (Log.isLoggable(tag, Log.WARN)) {
            Log.w(tag, str);
        }
    }
}
