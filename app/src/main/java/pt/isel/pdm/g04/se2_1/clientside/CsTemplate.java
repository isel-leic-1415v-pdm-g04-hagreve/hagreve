package pt.isel.pdm.g04.se2_1.clientside;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import pt.isel.pdm.g04.se2_1.helpers.HgBroadcast;
import pt.isel.pdm.g04.se2_1.helpers.HgUtil;
import pt.isel.pdm.g04.se2_1.provider.HgDbSchema;
import pt.isel.pdm.g04.se2_1.serverside.bags.HasId;

/**
 * Project SE2-1, created on 2015/04/15.
 */
public abstract class CsTemplate<T extends HasId> {

    protected final Context ctx;
    protected final ContentResolver contentResolver;

    public CsTemplate(Context ctx) {
        this.ctx = ctx;
        this.contentResolver = ctx.getContentResolver();
    }

    // region Abstract Methods

    public abstract T refactor(T item, Collection<T> items);

    protected abstract Uri getUri();

    protected abstract String[] getDefaultProjection();

    protected abstract ContentValues toContentValues(T item);

    protected abstract int[] getColumnIds(Cursor cursor);

    protected abstract T buildItem(Cursor cursor, int[] columnIds) throws ParseException;

    protected abstract void postAction(Context ctx, int status);

    // endregion Abstract Methods

    // region Template

    public void replaceAll(Collection<T> collection) {
        new AsyncTask<Collection<T>, Void, Void>() {
            @Override
            protected Void doInBackground(Collection<T>... params) {
                replaceAll_cbg(params[0]);
                return null;
            }
        }.execute(collection);
    }

    public Uri insert_cbg(T item) throws ParseException {
        HgUtil.not4UiThread();
        Uri _uri = getUri();
        ContentValues values = toContentValues(item);
        return contentResolver.insert(_uri, values);
    }

    public T load_cbg(int id) throws ParseException {
        HgUtil.not4UiThread();
        Uri _uri = getUri();
        Cursor _cursor = contentResolver.query(_uri, getDefaultProjection(),
                HgDbSchema.COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null);
        T _item = null;
        int[] _columnIds = getColumnIds(_cursor);
        if (_cursor.moveToFirst()) _item = buildItem(_cursor, _columnIds);
        _cursor.close();
        return _item;
    }

    public Collection<T> load_cbg(String selection, String[] selectionArguments) throws ParseException {
        HgUtil.not4UiThread();
        Uri _uri = getUri();
        Cursor _cursor = contentResolver.query(_uri, getDefaultProjection(), selection, selectionArguments, null);
        Collection<T> _collection = new ArrayList<>();
        int[] _columnIds = getColumnIds(_cursor);
        while (_cursor.moveToNext())
            _collection.add(buildItem(_cursor, _columnIds));
        _cursor.close();
        return _collection;
    }

    public int update_cbg(T item) throws ParseException {
        HgUtil.not4UiThread();
        Uri _uri = getUri();
        ContentValues values = toContentValues(item);
        int _count;
        _count = contentResolver.update(_uri,
                values,
                HgDbSchema.COL_ID,
                new String[]{String.valueOf(item.getId())});
        return _count;
    }

    public int replace_cbg(Collection<T> items) throws ParseException {
        HgUtil.not4UiThread();
        Uri _uri = getUri();
        ContentValues[] contentValueses = toContentValuesArray(items);
        return contentResolver.bulkInsert(_uri, contentValueses);
    }

    public void prepareInsertBatch(Collection<T> items, ArrayList<ContentProviderOperation> ops) throws ParseException {
        for (T item : items) {
            ops.add(ContentProviderOperation
                    .newInsert(getUri())
                    .withValues(toContentValues(item))
                    .build());
        }
    }

    public void prepareUpdateBatch(Collection<T> items, ArrayList<ContentProviderOperation> ops) throws ParseException {
        for (T item : items) {
            ops.add(ContentProviderOperation
                    .newUpdate(Uri.withAppendedPath(getUri(), String.valueOf(item.getId())))
                    .withValues(toContentValues(item))
                    .build());
        }
    }

    public int delete_cbg(T item) throws ParseException {
        HgUtil.not4UiThread();
        Uri _uri = Uri.withAppendedPath(getUri(), String.valueOf(item.getId()));
        return contentResolver.delete(_uri,
                HgDbSchema.COL_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    public int delete_cbg(Collection<T> items) throws ParseException {
        HgUtil.not4UiThread();
        int _count = 0;
        for (T item : items) _count += delete_cbg(item);
        return _count;
    }

    public void prepareDeleteBatch(Collection<T> items, ArrayList<ContentProviderOperation> ops) throws ParseException {
        for (T item : items) {
            ContentProviderOperation.Builder cpob = ContentProviderOperation
                    .newDelete(Uri.withAppendedPath(getUri(), String.valueOf(item.getId())));
        }
    }

    public void deleteAll_cbg() {
        HgUtil.not4UiThread();
        contentResolver.delete(getUri(), null, null);
    }

    private void insertAll_cbg(Uri uri, ContentValues[] values) {
        HgUtil.not4UiThread();
        contentResolver.bulkInsert(uri, values);
    }

    public void replaceAll_cbg(Collection<T> collection) {
        Uri _uri = getUri();
        ContentValues[] _contentValueses = toContentValuesArray(collection);
        insertAll_cbg(_uri, _contentValueses);
        postAction(ctx, _contentValueses.length);
    }

    public void upsert_cbg(Collection<T> collection) {
        HgUtil.not4UiThread();
        Uri _uri = getUri();
        ContentValues[] _contentValueses = toContentValuesArray(collection);
        insertAll_cbg(_uri, _contentValueses);
        postAction(ctx, _contentValueses.length);
    }

    public void update_cbg(Collection<T> collection) throws ParseException {
        HgUtil.not4UiThread();
        Collection<T> _toDelete = load_cbg(null, new String[0]);
        int _updated = 0;
        for (T item : collection) {
            if (!(item instanceof HasId)) return;
            if (_toDelete.contains(item)) _toDelete.remove(item);
            if (load_cbg(item.getId()) == null) _updated += insert_cbg(item) == null ? 0 : 1;
            else _updated += update_cbg(item);
        }
        _updated += delete_cbg(_toDelete);
        postAction(ctx, _updated);
    }

    // endregion Template

    // region Implementation Helpers

    protected void notifyListeners(String id, String field, int value) {
        HgBroadcast.send(ctx, id, field, value);
    }

    protected void notifyListeners(String id, String field, int[] values) {
        HgBroadcast.send(ctx, id, field, values);
    }

    protected ContentValues[] toContentValuesArray(Collection<T> collection) {
        ContentValues[] _results = new ContentValues[collection.size()];
        Iterator<T> _iterator = collection.iterator();
        for (int i = 0; _iterator.hasNext(); i++) {
            T _item = _iterator.next();
            _results[i] = toContentValues(_item);
        }
        return _results;
    }

    // endregion Implementation Helpers
}
