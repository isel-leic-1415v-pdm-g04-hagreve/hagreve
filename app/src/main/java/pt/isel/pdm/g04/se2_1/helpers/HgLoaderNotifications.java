package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.net.Uri;

public class HgLoaderNotifications {

    public static void notifyChange(Context ctx, Uri uri) {
        ctx.getContentResolver().notifyChange(uri, null);
    }

}
