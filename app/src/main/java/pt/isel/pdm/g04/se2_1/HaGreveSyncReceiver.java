package pt.isel.pdm.g04.se2_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.isel.pdm.g04.se2_1.helpers.G4Log;

public class HaGreveSyncReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        G4Log.i("HaGreveSyncReceiver");
        HaGreveServices.startActionSynchronizeAndNotify(ctx);
    }
}
