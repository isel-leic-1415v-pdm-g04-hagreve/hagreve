package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Project se2-1, created on 2015/05/05.
 */
public class G4Timeout {
    public final static String SP_TIMEOUT = "timeout";

    public static void expireTimeout(Context ctx) {
        expireTimeout(G4SharedPreferences.getDefault(ctx));
    }

    public static void expireTimeout(SharedPreferences sp) {
        resetTimeout(sp, 0);
    }

    public static void resetTimeout(Context ctx) {
        resetTimeout(G4SharedPreferences.getDefault(ctx));
    }

    public static void resetTimeout(SharedPreferences sp) {
        resetTimeout(sp, G4Defaults.TIMEOUT);
    }

    public static void resetTimeout(Context ctx, long targetWindowMillis) {
        resetTimeout(G4SharedPreferences.getDefault(ctx), targetWindowMillis);
    }

    public static void resetTimeout(SharedPreferences sharedPreferences, long targetWindowMillis) {
        sharedPreferences.edit().putLong(SP_TIMEOUT, calcTimeout(targetWindowMillis)).commit();
    }

    public static boolean isTimeout(Context ctx) {
        return isTimeout(G4SharedPreferences.getDefault(ctx));
    }

    public static boolean isTimeout(SharedPreferences sharedPreferences) {
        return sharedPreferences.getLong(SP_TIMEOUT, 0) < new Date().getTime();
    }

    private static long calcTimeout(long targetWindowMillis) {
        return new Date().getTime() + targetWindowMillis;
    }

}
