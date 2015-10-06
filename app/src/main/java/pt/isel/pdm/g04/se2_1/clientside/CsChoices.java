package pt.isel.pdm.g04.se2_1.clientside;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import pt.isel.pdm.g04.se2_1.clientside.bags.Choice;
import pt.isel.pdm.g04.se2_1.helpers.HgBroadcast;
import pt.isel.pdm.g04.se2_1.helpers.HgDefaults;
import pt.isel.pdm.g04.se2_1.helpers.HgLog;
import pt.isel.pdm.g04.se2_1.helpers.HgUtil;
import pt.isel.pdm.g04.se2_1.provider.HgContract;

/**
 * Project SE2-1, created on 2015/04/15.
 */
public class CsChoices extends CsTemplate<Choice> {

    public CsChoices(Context ctx) {
        super(ctx);
    }

    // region Abstract Methods Implementation

    @Override
    public Choice refactor(Choice item, Collection<Choice> items) {
        return item;
    }

    @Override
    protected Uri getUri() {
        return HgContract.Choices.CONTENT_URI;
    }

    @Override
    protected String[] getDefaultProjection() {
        return HgContract.Choices.PROJECTION_ALL;
    }

    @Override
    protected int[] getColumnIds(Cursor cursor) {
        return new int[] { cursor.getColumnIndex(HgContract.Choices._ID) };
    }

    @Override
    protected Choice buildItem(Cursor cursor, int[] columnIds) {
        return new Choice(cursor.getInt(columnIds[0]));
    }

    @Override
    protected ContentValues toContentValues(Choice item) {
        ContentValues _contentValues = new ContentValues();
        _contentValues.put(HgContract.Choices._ID, item.getId());
        return _contentValues;
    }

    @Override
    protected void postAction(Context ctx, int status) {
        HgBroadcast.send(ctx,
                HgDefaults.CHOICES_SEL,
                HgDefaults.CHOICES_SEL_MSG,
                status > 0 ? HgDefaults.DISPLAY : HgDefaults.HIDE
        );
    }

    // endregion Abstract Methods Implementation

    // region Choices

    public void replaceAll(int[] checkedCompaniesIds) {
        Collection<Choice> _collection = new ArrayList<>();
        for (int i : checkedCompaniesIds) _collection.add(new Choice(i));
        super.replaceAll(_collection);
    }

    public void loadAll() {
        new AsyncTask<Void, Void, Collection<Choice>>() {
            @Override
            protected Collection<Choice> doInBackground(Void... params) {
                try {
                    return load_cbg(null, new String[0]);
                } catch (ParseException e) {
                    HgLog.e("ParseException - returning an empty collection");
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
            @Override
            protected void onPostExecute(Collection<Choice> values) {
                notifyListeners(HgDefaults.CHOICES_UPD, HgDefaults.CHOICES_UPD_MSG, HgUtil.toIntArray(values));
            }
        }.execute();
    }

    public void check() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... noParams) {
                try {
                    notifyListeners(HgDefaults.CHOICES_SEL, HgDefaults.CHOICES_SEL_MSG,
                            load_cbg(null, new String[0]).size() > 0 ? HgDefaults.DISPLAY : HgDefaults.HIDE);
                } catch (ParseException e) {
                    notifyListeners(HgDefaults.CHOICES_SEL, HgDefaults.CHOICES_SEL_MSG, HgDefaults.HIDE);
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    // endregion Choices

}
