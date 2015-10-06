package pt.isel.pdm.g04.se2_1;

import android.app.Application;
import android.content.Intent;

import pt.isel.pdm.g04.se2_1.helpers.HgBroadcastAlarmManager;
import pt.isel.pdm.g04.se2_1.helpers.HgDefs;
import pt.isel.pdm.g04.se2_1.helpers.HgLog;

/**
 * Project SE2-1, created on 2015/04/22.
 */
public class HaGreveApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HgLog.i("HaGreveApplication ran");
        Intent _intent = new Intent(this, HaGreveSyncReceiver.class);
        HgBroadcastAlarmManager _alarm = new HgBroadcastAlarmManager(this, _intent, 0);
        if (!_alarm.isSet()) _alarm.set(6, 12, 1, HgDefs.HR);
    }
}
