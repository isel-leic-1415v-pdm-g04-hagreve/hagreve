package pt.isel.pdm.g04.se2_1.clientside;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import pt.isel.pdm.g04.se2_1.helpers.G4Defs;
import pt.isel.pdm.g04.se2_1.provider.HgContract;
import pt.isel.pdm.g04.se2_1.serverside.bags.Strike;

/**
 * Project SE2-1, created on 2015/04/15.
 */
public class CsStrikes extends CsTemplate<Strike> {

    public CsStrikes(Context ctx) {
        super(ctx);
    }

    // region Template

    @Override
    public Strike refactor(Strike item, Collection<Strike> items) {
        return item;
    }

    @Override
    protected Uri getUri() {
        return HgContract.Strikes.CONTENT_URI;
    }

    @Override
    protected String[] getDefaultProjection() {
        return HgContract.Strikes.PROJECTION_ALL;
    }

    @Override
    protected int[] getColumnIds(Cursor cursor) {
        return new int[]{
                cursor.getColumnIndex(HgContract.Strikes._ID),
                cursor.getColumnIndex(HgContract.Strikes.COMPANY),
                cursor.getColumnIndex(HgContract.Strikes.DATE_FROM),
                cursor.getColumnIndex(HgContract.Strikes.DATE_TO),
                cursor.getColumnIndex(HgContract.Strikes.ALL_DAY),
                cursor.getColumnIndex(HgContract.Strikes.CANCELLED),
                cursor.getColumnIndex(HgContract.Strikes.SOURCE_LINK),
                cursor.getColumnIndex(HgContract.Strikes.DESCRIPTION),
                cursor.getColumnIndex(HgContract.Strikes.SUBMITTER)
        };
    }

    @Override
    protected Strike buildItem(Cursor cursor, int[] columnIds) throws ParseException {
        return Strike.build(ctx, cursor);
    }

    @Override
    protected ContentValues toContentValues(Strike item) {
        DateFormat df = new SimpleDateFormat(G4Defs.DATETIME_14_STRING_FORMAT);
        ContentValues contentValues = new ContentValues();
        contentValues.put(HgContract.Strikes._ID, item.id);
        contentValues.put(HgContract.Strikes.COMPANY, item.company.id);
        contentValues.put(HgContract.Strikes.DATE_FROM, df.format(item.start_date));
        contentValues.put(HgContract.Strikes.DATE_TO, df.format(item.end_date));
        contentValues.put(HgContract.Strikes.ALL_DAY, item.all_day);
        contentValues.put(HgContract.Strikes.CANCELLED, item.canceled);
        contentValues.put(HgContract.Strikes.DESCRIPTION, item.description);
        contentValues.put(HgContract.Strikes.SOURCE_LINK, item.source_link);
//        contentValues.put(HgContract.Strikes.SUBMITTER_ID, item.submitter.);
        return contentValues;
    }

    @Override
    public void deleteAll_cbg() {
        super.deleteAll_cbg();
        contentResolver.delete(HgContract.Notifications.CONTENT_URI, null, null);
    }

    @Override
    protected void postAction(Context ctx, int status) {
    }

    // endregion Template

}
