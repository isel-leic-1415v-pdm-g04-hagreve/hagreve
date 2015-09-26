package pt.isel.pdm.g04.se2_1.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import static pt.isel.pdm.g04.se2_1.helpers.G4Util.generateRandom;

/**
 * Project SE2-1, created on 2015/04/28.
 */
public class G4BroadcastAlarmManager {

    public static final long DEFAULT_INITAL_DELAY = 12 * G4Defs.SEC;

    private final Context ctx;
    private final Intent intent;
    private final int requestCode;

    public G4BroadcastAlarmManager(Context ctx, Intent intent, int requestCode) {
        this.ctx = ctx;
        this.intent = intent;
        this.requestCode = requestCode;
    }

    public boolean isSet() {
        return getBroadcastPendingIntent(PendingIntent.FLAG_NO_CREATE) != null;
    }

    public void set(long interval) {
        set(interval, DEFAULT_INITAL_DELAY, G4Defs.MILLISEC);
    }

    public void set(long interval, long units) {
        set(interval, DEFAULT_INITAL_DELAY, units);
    }

    public void set(long interval, long initialDelay, long units) {
        set(interval, initialDelay, generateRandom(), units);
    }

    public void set(long interval, long initialDelay, long intervalShift, long units) {
        if (isSet()) clear();
        PendingIntent _pendingIntent = getBroadcastPendingIntent(0);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + initialDelay,
                (interval + generateRandom(intervalShift)) * units,
                _pendingIntent);
    }

    public void clear() {
        PendingIntent _pendingIntent = getBroadcastPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(_pendingIntent);
    }

    private PendingIntent getBroadcastPendingIntent(int flag) {
        return PendingIntent.getBroadcast(ctx, requestCode, intent, flag);
    }
}
