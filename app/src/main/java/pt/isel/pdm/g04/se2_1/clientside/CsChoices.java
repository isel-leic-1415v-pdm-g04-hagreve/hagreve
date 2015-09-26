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
import pt.isel.pdm.g04.se2_1.helpers.G4Broadcast;
import pt.isel.pdm.g04.se2_1.helpers.G4Defaults;
import pt.isel.pdm.g04.se2_1.helpers.G4Log;
import pt.isel.pdm.g04.se2_1.helpers.G4Util;
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
        G4Broadcast.send(ctx,
                G4Defaults.CHOICES_SEL,
                G4Defaults.CHOICES_SEL_MSG,
                status > 0 ? G4Defaults.DISPLAY : G4Defaults.HIDE
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
                    G4Log.e("ParseException - returning an empty collection");
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }
            @Override
            protected void onPostExecute(Collection<Choice> values) {
                notifyListeners(G4Defaults.CHOICES_UPD, G4Defaults.CHOICES_UPD_MSG, G4Util.toIntArray(values));
            }
        }.execute();
    }

    public void check() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... noParams) {
                try {
                    notifyListeners(G4Defaults.CHOICES_SEL, G4Defaults.CHOICES_SEL_MSG,
                            load_cbg(null, new String[0]).size() > 0 ? G4Defaults.DISPLAY : G4Defaults.HIDE);
                } catch (ParseException e) {
                    notifyListeners(G4Defaults.CHOICES_SEL, G4Defaults.CHOICES_SEL_MSG, G4Defaults.HIDE);
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    // endregion Choices

}
