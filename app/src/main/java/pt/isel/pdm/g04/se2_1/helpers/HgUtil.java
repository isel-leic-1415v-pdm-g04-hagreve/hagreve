package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Looper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import pt.isel.pdm.g04.se2_1.BuildConfig;
import pt.isel.pdm.g04.se2_1.serverside.bags.HasId;

public class HgUtil {

    public static <T extends HasId> int[] toIntArray(Collection<T> collection) {
        int[] _results = new int[collection.size()];
        int _i = 0;
        for (HasId item : collection) _results[_i++] = item.getId();
        return _results;
    }

    public static Collection<Integer> toCollection(int[] values) {
        Collection<Integer> _results = new ArrayList<>();
        for (int value : values) _results.add(value);
        return _results;
    }

    public static String dateFormat(String date) throws ParseException {
        return dateFormat(date, HgDefs.MONTHDAY_5_STRING_FORMAT);
    }

    public static String dateFormat(String date, String targetFormat) throws ParseException {
        return dateFormat(date, HgDefs.DATE_8_STRING_FORMAT, targetFormat);
    }

    public static String dateFormat(String date, String sourceFormat, String targetFormat) throws ParseException {
        DateFormat _dateParser = new SimpleDateFormat(sourceFormat, Locale.getDefault());
        DateFormat _dateFormatter = new SimpleDateFormat(targetFormat, Locale.getDefault());
        return _dateFormatter.format(_dateParser.parse(date));
    }

    public static boolean isWiFi(Context ctx) {
        ConnectivityManager _cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        return _cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static long generateRandom() {
        return generateRandom(1);
    }

    public static long generateRandom(long range) {
        return (long) (Math.random() * range);
    }

    public static boolean isUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static void not4UiThread() {
        if (BuildConfig.DEBUG && HgUtil.isUiThread()) {
            throw new IllegalThreadStateException("This should not be Running on the UI thread!");
        }
    }
}
