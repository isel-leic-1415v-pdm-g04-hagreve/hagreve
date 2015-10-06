package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;

public class HgSyncRequirements {

    public static final short NONE    = ~0x00;
    public static final short BATTERY = ~0x01;
    public static final short WIFI    = ~0x02;

    public static boolean isOk(Context ctx, int requirements) {
        boolean isOk = true;
        if ((requirements & ~BATTERY) == 0) isOk &= HgBattery.isOkay(ctx);
        if ((requirements & ~WIFI) == 0) isOk &= HgUtil.isWiFi(ctx);
        return isOk;
    }

}
