package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.BatteryManager;

/**
 * Project SE2-1, created on 2015/04/25.
 */
public class G4Battery {

    private final static String SP_BATTERY_OKAY = "battery_okay";
    private final static String SP_BATTERY_LEVEL = "battery_level";

    public static void saveStatus(Context ctx, Intent intent) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                if (params.length != 2) throw new IllegalArgumentException();
                Context ctx = (Context) params[0];
                Intent intent = (Intent) params[1];
                int _level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int _scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int _level0to100 = Math.round(100 * (_level / (float)_scale));
                String _level0to100String = String.valueOf(_level0to100);
                G4Log.i("Battery level: " + _level0to100String);
                G4SharedPreferences.getDefault(ctx)
                        .edit()
                        .putBoolean(SP_BATTERY_OKAY, intent.getAction().equals(Intent.ACTION_BATTERY_OKAY))
                        .putInt(SP_BATTERY_LEVEL, _level0to100)
                        .commit();
                return null;
            }
        }.execute(ctx, intent);
    }

    public static boolean isOkay(Context ctx) {
        return G4SharedPreferences.getDefault(ctx).getBoolean(SP_BATTERY_OKAY, true);
    }

    public static int getLevel(Context ctx) {
        return G4SharedPreferences.getDefault(ctx).getInt(SP_BATTERY_LEVEL, 100);
    }

    public static void resetStatus(Context ctx) {
        new AsyncTask<Context, Void, Void>() {
            @Override
            protected Void doInBackground(Context... ctxs) {
                if (ctxs.length != 2) throw new IllegalArgumentException();
                G4SharedPreferences.getDefault(ctxs[0])
                        .edit()
                        .remove(SP_BATTERY_OKAY)
                        .remove(SP_BATTERY_LEVEL)
                        .commit();
                return null;
            }
        }.execute(ctx);
    }

}
