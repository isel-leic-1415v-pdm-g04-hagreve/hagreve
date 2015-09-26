package pt.isel.pdm.g04.se2_1.clientside;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.provider.HgContract;
import pt.isel.pdm.g04.se2_1.serverside.bags.Company;

/**
 * Project SE2-1, created on 2015/04/15.
 */
public class CsCompanies extends CsTemplate<Company> {

    public CsCompanies(Context ctx) {
        super(ctx);
    }

    // region Abstract Methods Implementation

    @Override
    public Company refactor(Company item, Collection<Company> items) {
        for (Company company : items)
            if (company.name.equals(item.name)) return Company.builder()
                .id(item.id)
                .name(item.name + " (" + String.valueOf(item.id) + ")")
                .build();
        return item;
    }

    @Override
    protected Uri getUri() {
        return HgContract.Companies.CONTENT_URI;
    }

    @Override
    protected String[] getDefaultProjection() {
        return HgContract.Companies.PROJECTION_ALL;
    }

    @Override
    protected int[] getColumnIds(Cursor cursor) {
        return new int[]{
                cursor.getColumnIndex(HgContract.Companies._ID),
                cursor.getColumnIndex(HgContract.Companies.NAME)
        };
    }

    @Override
    protected Company buildItem(Cursor cursor, int[] columnIds) {
        return Company.builder()
                .id(cursor.getInt(columnIds[0]))
                .name(cursor.getString(columnIds[1]))
                .build();
    }

    @Override
    protected ContentValues toContentValues(Company item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HgContract.Companies._ID, item.id);
        contentValues.put(HgContract.Companies.NAME, item.name);
        return contentValues;
    }

    @Override
    protected void postAction(Context ctx, int status) {
    }

    // endregion Abstract Methods Implementation


    @Override
    public void deleteAll_cbg() {
        super.deleteAll_cbg();
        ctx.getContentResolver().delete(HgContract.Choices.CONTENT_URI, null, null);
    }
}
