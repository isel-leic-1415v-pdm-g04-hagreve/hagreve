package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Project SE2-1, created on 2015/04/16.
 */
public class G4Broadcast {

    public static void send(Context ctx, String id, String label, int value) {
        Intent intent = new Intent(id);
        intent.putExtra(label, value);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
    }

    public static void send(Context ctx, String id, String label, int[] values) {
        Intent intent = new Intent(id);
        intent.putExtra(label, values);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
}   }
