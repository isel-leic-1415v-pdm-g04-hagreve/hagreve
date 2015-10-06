package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;

public class HgSharedPreferences {

    // region public support methods

    public static SharedPreferences getDefault(Context ctx) {
        return ctx.getSharedPreferences(HgDefaults.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    // endregion public support methods

    public static String getDefaultTag(Context ctx, String tag, String defaultValue) {
        return getDefaultTag(getDefault(ctx), tag, defaultValue);
    }

    public static String getDefaultTag(SharedPreferences sharedPreferences, String tag, String defaultValue) {
        return sharedPreferences.getString(tag, defaultValue);
    }

    public static int getDefaultTag(Context ctx, String tag, int defaultValue) {
        return getDefaultTag(getDefault(ctx), tag, defaultValue);
    }

    public static int getDefaultTag(SharedPreferences sharedPreferences, String tag, int defaultValue) {
        return sharedPreferences.getInt(tag, defaultValue);
    }

    public static boolean getDefaultTag(Context ctx, String tag, boolean defaultValue) {
        return getDefaultTag(getDefault(ctx), tag, defaultValue);
    }

    public static boolean getDefaultTag(SharedPreferences sharedPreferences, String tag, boolean defaultValue) {
        return sharedPreferences.getBoolean(tag, defaultValue);
    }

    // region internal support methods

    // region String

    private static void storeString(Context ctx, String key, String jsonString)
            throws JSONException {
        storeString(getDefault(ctx), key, jsonString);
    }

    private static void storeString(SharedPreferences sharedPreferences, String key,
                                    String jsonString) throws JSONException {
        sharedPreferences.edit().putString(key, jsonString).commit();
    }

    private static String restoreString(Context ctx, String key) throws JSONException {
        return restoreString(getDefault(ctx), key);
    }

    private static String restoreString(SharedPreferences sharedPreferences, String key)
            throws JSONException {
        return sharedPreferences.getString(key, "[]");
    }

    private static boolean isStoredString(Context ctx, String key) {
        return isStoredString(getDefault(ctx), key);
    }

    private static boolean isStoredString(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.contains(key);
    }

    // endregion String


}
