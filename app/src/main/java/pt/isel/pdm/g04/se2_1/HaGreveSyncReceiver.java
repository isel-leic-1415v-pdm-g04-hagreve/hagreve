package pt.isel.pdm.g04.se2_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.isel.pdm.g04.se2_1.helpers.HgLog;

public class HaGreveSyncReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        HgLog.i("HaGreveSyncReceiver");
        HaGreveServices.startActionSynchronizeAndNotify(ctx);
    }
}
