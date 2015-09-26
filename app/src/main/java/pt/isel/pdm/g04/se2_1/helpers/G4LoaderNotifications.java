package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.net.Uri;

import java.util.Collection;

/**
 * Project SE2-1, created on 2015/04/21.
 */
public class G4LoaderNotifications {

    public static void notifyChange(Context ctx, Collection<Uri> uris) {
        notifyChange(ctx, uris.toArray(new Uri[uris.size()]));
    }

    public static void notifyChange(Context ctx, Uri... uris) {
        for (Uri uri : uris) notifyChange(ctx, uri);
    }

    public static void notifyChange(Context ctx, Uri uri) {
        ctx.getContentResolver().notifyChange(uri, null);
}   }
