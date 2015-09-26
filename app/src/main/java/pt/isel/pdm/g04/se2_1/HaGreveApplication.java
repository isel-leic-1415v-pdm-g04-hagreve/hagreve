package pt.isel.pdm.g04.se2_1;

import android.app.Application;
import android.content.Intent;

import pt.isel.pdm.g04.se2_1.helpers.G4BroadcastAlarmManager;
import pt.isel.pdm.g04.se2_1.helpers.G4Log;
import pt.isel.pdm.g04.se2_1.helpers.G4Defs;

/**
 * Project SE2-1, created on 2015/04/22.
 */
public class HaGreveApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        G4Log.i("HaGreveApplication ran");
        Intent _intent = new Intent(this, HaGreveSyncReceiver.class);
        G4BroadcastAlarmManager _alarm = new G4BroadcastAlarmManager(this, _intent, 0);
        if (!_alarm.isSet()) _alarm.set(6, 12, 1, G4Defs.HR);
    }
}
