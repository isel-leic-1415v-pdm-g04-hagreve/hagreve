package pt.isel.pdm.g04.se2_1.helpers;

import android.util.Log;

/**
 * Project SE2-1, created on 2015/03/18.
 */
public class G4Log {

    // region Logging

    public static final String LOG_TAG = "PDM";

    public static void d(String message) {
        Log.d(LOG_TAG, message);
    }

    public static void i(String message) {
        Log.i(LOG_TAG, message);
    }

    public static void e(String message) {
        Log.e(LOG_TAG, message);
    }

    // endregion Logging

}
