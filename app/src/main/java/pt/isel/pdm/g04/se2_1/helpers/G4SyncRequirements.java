package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;

/**
 * Project SE2-1, created on 2015/04/25.
 */
public class G4SyncRequirements {

    public static final short NONE    = ~0x00;
    public static final short BATTERY = ~0x01;
    public static final short WIFI    = ~0x02;

    public static boolean isOk(Context ctx, int requirements) {
        boolean isOk = true;
        if ((requirements & ~BATTERY) == 0) isOk &= G4Battery.isOkay(ctx);
        if ((requirements & ~WIFI) == 0) isOk &= G4Util.isWiFi(ctx);
        return isOk;
    }

}
