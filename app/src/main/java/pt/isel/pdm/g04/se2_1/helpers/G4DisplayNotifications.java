package pt.isel.pdm.g04.se2_1.helpers;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.MainActivity;
import pt.isel.pdm.g04.se2_1.R;
import pt.isel.pdm.g04.se2_1.StrikeDetailsActivity;
import pt.isel.pdm.g04.se2_1.provider.HgContract;
import pt.isel.pdm.g04.se2_1.serverside.bags.Strike;

/**
 * Project SE2-1, created on 2015/04/23.
 */
public class G4DisplayNotifications {

    public static final int DEFAULT_SITUATION = R.drawable.ic_stat_strike;
    public static final int NEW_STRIKE = R.drawable.ic_stat_new_strike;
    public static final int STRIKE_CANCELLED = R.drawable.ic_stat_strike_cancelled;
    public static final int STRIKE_UPDATED = R.drawable.ic_stat_strike_updated;
    public static final int STRIKE_TODAY = R.drawable.ic_stat_strike_today;
    public static final int STRIKE_COMING = R.drawable.ic_stat_strike_coming;

    public static void notify(Context ctx, Collection<Strike> items) {
        notify(ctx, items, DEFAULT_SITUATION);
    }

    public static void notify(Context ctx, Collection<Strike> items, int situation) {
        for (Strike item : items) notify(ctx, item, situation);
    }

    static int ref = 0;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void notify(Context ctx, Strike strike, int situation) {
        if (situation == STRIKE_TODAY && strike.canceled) return;
        if (strike.canceled) situation = STRIKE_CANCELLED;
        Intent _strikesIntent = new Intent(ctx, MainActivity.class);
        Intent _strikeDetailsIntent = new Intent(ctx, StrikeDetailsActivity.class);
        String _strikeId = String.valueOf(strike.getId());
        Uri _uri = Uri.withAppendedPath(HgContract.StrikesDetails_vw.CONTENT_URI, _strikeId);
        _strikeDetailsIntent.putExtra(StrikeDetailsActivity.EXTRA_URI, _uri.toString());
        PendingIntent _pendingIntent = TaskStackBuilder.create(ctx)
                .addNextIntentWithParentStack(_strikesIntent)
                .addNextIntentWithParentStack(_strikeDetailsIntent)
                .getPendingIntent(ref ++, PendingIntent.FLAG_UPDATE_CURRENT, null);
        Notification _notification = new NotificationCompat.Builder(ctx)
                .setContentTitle(strike.get(Strike.LBL_COMPANY))
                .setContentText(strike.canceled ?
                        ctx.getString(R.string.am_msg_cancelled) :
                        strike.get(Strike.LBL_DAY_FROM) + " " + strike.get(Strike.LBL_MONTH_FROM) +
                                (strike.get(Strike.LBL_DAY_TO).equals("") ?
                                        "" :
                                        " - " + strike.get(Strike.LBL_DAY_TO) + " " + strike.get(Strike.LBL_MONTH_TO)) )
                .setSmallIcon(situation)
                .setAutoCancel(true)
                .setContentIntent(_pendingIntent)
                .build();
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(strike.getId(), _notification);
}   }
