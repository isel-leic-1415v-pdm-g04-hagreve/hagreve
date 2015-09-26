package pt.isel.pdm.g04.se2_1.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;

import pt.isel.pdm.g04.se2_1.helpers.G4LoaderNotifications;

/**
 * Project SE2-1, created on 2015/04/13.
 *
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of João Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */
public class HgProvider extends ContentProvider {

    private final ThreadLocal<BatchBag> mBatchBag = new ThreadLocal<BatchBag>() {
        public BatchBag batchBag;
        @Override
        protected BatchBag initialValue() {
            batchBag = new BatchBag();
            return batchBag;
        }
    };
    private BatchBag mBag = mBatchBag.get();

    private static final String UNEXPECTED_MATCH = "Internal error: Unexpected matcher match";
    private static final String UNMATCHED_MATCH = "Internal error: Unmatched matcher match";

    private Context mContext;
//    private ContentResolver mContentResolver;

    protected static final int TIMEOUT_LST = 100;
    protected static final int TIMEOUT_OBJ = 101;
    protected static final int CHOICES_LST = 200;
    protected static final int CHOICES_OBJ = 201;
    protected static final int COMPANIES_LST = 300;
    protected static final int COMPANIES_OBJ = 301;
    protected static final int LOGOS_LST = 350;
    protected static final int LOGOS_OBJ = 351;
    protected static final int STRIKES_LST = 400;
    protected static final int STRIKES_OBJ = 401;
    protected static final int STRIKES_VW_LST = 410;
    protected static final int STRIKES_VW_OBJ = 411;
    protected static final int STRIKES_DETAILS_VW_LST = 420;
    protected static final int STRIKES_DETAILS_VW_OBJ = 421;
    protected static final int NOTIFICATIONS_LST = 500;
    protected static final int NOTIFICATIONS_OBJ = 501;
    protected static final int UN_STRIKES_LST = 600;
    protected static final int UN_STRIKES_OBJ = 601;

    private static final String UNIT = "/#";

    protected static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Timeouts.RESOURCE, TIMEOUT_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Timeouts.RESOURCE + UNIT, TIMEOUT_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Choices.RESOURCE, CHOICES_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Choices.RESOURCE + UNIT, CHOICES_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Companies.RESOURCE, COMPANIES_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Companies.RESOURCE + UNIT, COMPANIES_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Logos.RESOURCE, LOGOS_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Logos.RESOURCE + UNIT, LOGOS_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Strikes.RESOURCE, STRIKES_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Strikes.RESOURCE + UNIT, STRIKES_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Strikes_vw.RESOURCE, STRIKES_VW_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Strikes_vw.RESOURCE + UNIT, STRIKES_VW_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.StrikesDetails_vw.RESOURCE, STRIKES_DETAILS_VW_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.StrikesDetails_vw.RESOURCE + UNIT, STRIKES_DETAILS_VW_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Notifications.RESOURCE, NOTIFICATIONS_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.Notifications.RESOURCE + UNIT, NOTIFICATIONS_OBJ);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.UnnotifiedStrikes_vw.RESOURCE, UN_STRIKES_LST);
        sUriMatcher.addURI(HgContract.AUTHORITY, HgContract.UnnotifiedStrikes_vw.RESOURCE + UNIT, UN_STRIKES_OBJ);
    }

    private HgDbOpenHelper mDbHelper = null;

    @Override
    public boolean onCreate() {
        mContext = getContext();
//        mContentResolver = mContext.getContentResolver();
        mDbHelper = new HgDbOpenHelper(mContext);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case STRIKES_DETAILS_VW_LST: return HgContract.Strikes_vw.CONTENT_TYPE;
            case STRIKES_DETAILS_VW_OBJ: return HgContract.Strikes_vw.CONTENT_ITEM_TYPE;
            case STRIKES_VW_LST:         return HgContract.Strikes_vw.CONTENT_TYPE;
            case STRIKES_VW_OBJ:         return HgContract.Strikes_vw.CONTENT_ITEM_TYPE;
            case STRIKES_LST:            return HgContract.Strikes.CONTENT_TYPE;
            case STRIKES_OBJ:            return HgContract.Strikes.CONTENT_ITEM_TYPE;
            case COMPANIES_LST:          return HgContract.Companies.CONTENT_TYPE;
            case COMPANIES_OBJ:          return HgContract.Companies.CONTENT_ITEM_TYPE;
            case LOGOS_LST:              return HgContract.Logos.CONTENT_TYPE;
            case LOGOS_OBJ:              return HgContract.Logos.CONTENT_ITEM_TYPE;
            case CHOICES_LST:            return HgContract.Choices.CONTENT_TYPE;
            case CHOICES_OBJ:            return HgContract.Choices.CONTENT_ITEM_TYPE;
            case TIMEOUT_LST:            return HgContract.Timeouts.CONTENT_TYPE;
            case TIMEOUT_OBJ:            return HgContract.Timeouts.CONTENT_ITEM_TYPE;
            case NOTIFICATIONS_LST:      return HgContract.Notifications.CONTENT_TYPE;
            case NOTIFICATIONS_OBJ:      return HgContract.Notifications.CONTENT_ITEM_TYPE;
            case UN_STRIKES_LST:         return HgContract.UnnotifiedStrikes_vw.CONTENT_TYPE;
            case UN_STRIKES_OBJ:         return HgContract.UnnotifiedStrikes_vw.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase _db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder _qbuilder = new SQLiteQueryBuilder();
        int _match = sUriMatcher.match(uri);
        switch (_match) {

            case STRIKES_DETAILS_VW_LST:
                _qbuilder.setTables(HgDbSchema.StrikesDetails_vw.VIEW_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder =
                        HgDbSchema.StrikesDetails_vw.COL_DATE_FROM + " ASC, " +
                                HgDbSchema.StrikesDetails_vw.COL_COMPANY + " ASC";
                break;
            case STRIKES_DETAILS_VW_OBJ:
                _qbuilder.setTables(HgDbSchema.StrikesDetails_vw.VIEW_NAME);
                _qbuilder.appendWhere(HgDbSchema.COL_ID + "=" + uri.getLastPathSegment());
                break;
            case STRIKES_VW_LST:
                _qbuilder.setTables(HgDbSchema.Strikes_vw.VIEW_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder =
                        HgDbSchema.Strikes_vw.COL_DATE_FROM + " ASC, " +
                                HgDbSchema.Strikes_vw.COL_COMPANY + " ASC";
                break;
            case STRIKES_VW_OBJ:
                _qbuilder.setTables(HgDbSchema.Strikes_vw.VIEW_NAME);
                _qbuilder.appendWhere(HgDbSchema.COL_ID + "=" + uri.getLastPathSegment());
                break;
            case STRIKES_LST:
                _qbuilder.setTables(HgDbSchema.Strikes.TBL_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder =
                        HgDbSchema.Strikes.COL_DATE_FROM + " ASC, " +
                                HgDbSchema.Strikes.COL_COMPANY + " ASC";
                break;
            case STRIKES_OBJ:
                _qbuilder.setTables(HgDbSchema.Strikes.TBL_NAME);
                _qbuilder.appendWhere(HgDbSchema.COL_ID + " = " + uri.getLastPathSegment());
                break;
            case UN_STRIKES_LST:
                _qbuilder.setTables(HgDbSchema.UnnotifiedStrikes_vw.VIEW_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder =
                        HgDbSchema.Strikes.COL_DATE_FROM + " ASC, " +
                                HgDbSchema.Strikes.COL_COMPANY + " ASC";
                break;
            case UN_STRIKES_OBJ:
                _qbuilder.setTables(HgDbSchema.UnnotifiedStrikes_vw.VIEW_NAME);
                _qbuilder.appendWhere(HgDbSchema.COL_ID + "=" + uri.getLastPathSegment());
                break;
            case COMPANIES_LST:
                _qbuilder.setTables(HgDbSchema.Companies.TBL_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder = HgContract.Companies.DEFAULT_SORT_ORDER;
                break;
            case COMPANIES_OBJ:
                throw new UnsupportedOperationException(UNEXPECTED_MATCH);
//                _qbuilder.setTables(HgDbSchema.Companies.TBL_NAME);
//                _qbuilder.appendWhere(HgDbSchema.COL_ID + "=" + uri.getLastPathSegment());
//                break;
            case LOGOS_LST:
                _qbuilder.setTables(HgDbSchema.Logos.TBL_NAME);
                break;
            case LOGOS_OBJ:
                _qbuilder.setTables(HgDbSchema.Logos.TBL_NAME);
                _qbuilder.appendWhere(HgDbSchema.COL_ID + "=" + uri.getLastPathSegment());
                break;
            case CHOICES_LST:
                _qbuilder.setTables(HgDbSchema.Choices.TBL_NAME);
                if (TextUtils.isEmpty(sortOrder)) sortOrder = null;
                break;
            case CHOICES_OBJ:
                throw new UnsupportedOperationException(UNEXPECTED_MATCH);
//                _qbuilder.setTables(HgDbSchema.Choices.TBL_NAME);
//                _qbuilder.appendWhere(HgDbSchema.COL_ID + "=" + uri.getLastPathSegment());
//                break;
            case TIMEOUT_LST:
                throw new UnsupportedOperationException(UNEXPECTED_MATCH);
//                _qbuilder.setTables(HgDbSchema.Timeouts.TBL_NAME);
//                if (TextUtils.isEmpty(sortOrder)) sortOrder = null;
//                break;
            case TIMEOUT_OBJ:
                throw new UnsupportedOperationException(UNEXPECTED_MATCH);
//                _qbuilder.setTables(HgDbSchema.Timeouts.TBL_NAME);
//                _qbuilder.appendWhere(HgDbSchema.COL_ID + "=" + uri.getLastPathSegment());
//                break;
            default:
                throw new IllegalStateException(UNMATCHED_MATCH);
        }
        Cursor _cursor = _qbuilder.query(_db, projection, selection, selectionArgs, null, null, sortOrder);
        _cursor.setNotificationUri(mContext.getContentResolver(), uri);
        return _cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String _table;
        boolean _chgStrikes, _chgCompanies, _chgChoices;
        _chgStrikes = _chgCompanies = _chgChoices = false;
        switch (sUriMatcher.match(uri)) {
            case STRIKES_LST:
                _chgStrikes = true;
                _table = HgDbSchema.Strikes.TBL_NAME;
                break;
            case COMPANIES_LST:
                _chgStrikes = true;
                _table = HgDbSchema.Companies.TBL_NAME;
                break;
            case LOGOS_LST:
                _chgStrikes = true;
                _table = HgDbSchema.Logos.TBL_NAME;
                break;
            case CHOICES_LST:
                _chgChoices = true;
                _table = HgDbSchema.Choices.TBL_NAME;
                mBag.isNotifyStrikes_vw = true;
                break;
            case TIMEOUT_LST:
                _table = HgDbSchema.Timeouts.TBL_NAME;
                break;
            case NOTIFICATIONS_LST:
                _table = HgDbSchema.Notifications.TBL_NAME;
                break;
            default:
                throw new IllegalArgumentException(UNMATCHED_MATCH);
        }

        SQLiteDatabase _db = mDbHelper.getWritableDatabase();
        long _rowId = _db.insert(_table, null, values);
        if (_rowId != -1) {
            if (_chgStrikes) mBag.isNotifyStrikes = mBag.isNotifyStrikes_vw = true;
            if (_chgCompanies) mBag.isNotifyCompanies = mBag.isNotifyCompanies_vw = true;
            if (_chgChoices) mBag.isNotifyStrikes = mBag.isNotifyStrikes_vw = true;
            if (!mBag.isBatchMode) handleNotifications();
            return ContentUris.withAppendedId(uri, _rowId);
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) throws SQLException {
        boolean _chgStrikes, _chgCompanies;
        _chgStrikes = _chgCompanies = false;
        String _table;
        switch (sUriMatcher.match(uri)) {
            case STRIKES_LST:
                _chgStrikes = true;
                _table = HgDbSchema.Strikes.TBL_NAME;
                break;
            case STRIKES_OBJ:
                _chgStrikes = true;
                _table = HgDbSchema.Strikes.TBL_NAME;
                break;
            case COMPANIES_LST:
                _chgCompanies = true;
                _table = HgDbSchema.Companies.TBL_NAME;
                break;
            case COMPANIES_OBJ:
                _chgCompanies = true;
                _table = HgDbSchema.Companies.TBL_NAME;
                break;
            case LOGOS_OBJ:
                _table = HgDbSchema.Logos.TBL_NAME;
                break;
            default:
                throw new IllegalArgumentException(UNMATCHED_MATCH);
        }

        if (selection == null) {
            selection = HgDbSchema.COL_ID + " = ?";
            selectionArgs = new String[] {String.valueOf(values.get(HgDbSchema.COL_ID))};
        }
        int _count = 0;
        SQLiteDatabase _db = mDbHelper.getWritableDatabase();
        _count = _db.update(_table, values, selection, selectionArgs);
        if (_count > 0) {
            if (_chgStrikes) mBag.isNotifyStrikes = mBag.isNotifyStrikes_vw = true;
            if (_chgCompanies) mBag.isNotifyCompanies = mBag.isNotifyCompanies_vw = true;
            if (!mBag.isBatchMode) handleNotifications();
        }
        return _count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        boolean _chgStrikes, _chgCompanies;
        _chgStrikes = _chgCompanies = false;
        String _table;
        switch (sUriMatcher.match(uri)) {
            case STRIKES_LST: case STRIKES_OBJ:
                _chgStrikes = true;
                _table = HgDbSchema.Strikes.TBL_NAME;
                break;
            case COMPANIES_LST:
                _chgCompanies = true;
                _table = HgDbSchema.Companies.TBL_NAME;
                break;
            case LOGOS_LST:
                _chgCompanies = true;
                _table = HgDbSchema.Logos.TBL_NAME;
                break;
            case NOTIFICATIONS_LST: case NOTIFICATIONS_OBJ:
                _table = HgDbSchema.Notifications.TBL_NAME;
                break;
            case CHOICES_LST:
                _chgStrikes = true;
                _table = HgDbSchema.Choices.TBL_NAME;
                break;
            case CHOICES_OBJ:
                _chgStrikes = true;
                _table = HgDbSchema.Choices.TBL_NAME;
                break;
            case TIMEOUT_LST: // Há que fazer concatenação de Strings o ID vem no URI
                selection = null;
            case TIMEOUT_OBJ:
                _table = HgDbSchema.Timeouts.TBL_NAME;
                break;
            default:
                throw new IllegalStateException(UNMATCHED_MATCH);
        }

        SQLiteDatabase _db = mDbHelper.getWritableDatabase();
        int _count = _db.delete(_table, selection, selectionArgs);
        if (_count > 0) {
            if (_chgStrikes) mBag.isNotifyStrikes = mBag.isNotifyStrikes_vw = true;
            if (_chgCompanies) mBag.isNotifyCompanies = mBag.isNotifyCompanies_vw = true;
            if (!mBag.isBatchMode) handleNotifications();
        }
        return _count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        String _table;
        switch (sUriMatcher.match(uri)) {
            case STRIKES_LST:
                _table = HgDbSchema.Strikes.TBL_NAME;
                mBag.isNotifyStrikes = mBag.isNotifyStrikes_vw = true;
                break;
            case COMPANIES_LST:
                _table = HgDbSchema.Companies.TBL_NAME;
                mBag.isNotifyCompanies = mBag.isNotifyCompanies_vw = true;
                break;
            case CHOICES_LST: case TIMEOUT_LST:
                throw new UnsupportedOperationException("Use insert instead");
            default:
                throw new IllegalArgumentException(UNMATCHED_MATCH);
        }

        int _rowsCount = 0;
        SQLiteDatabase _db = null;
        try {
            mBag.isBatchMode = true;
            _db = mDbHelper.getWritableDatabase();
            _db.beginTransaction();
            for (ContentValues contentValues : values) {
                long _rowId = _db.replace(_table, null, contentValues);
                if (_rowId != -1) _rowsCount++;
            }
            _db.setTransactionSuccessful();
        } finally {
            _db.endTransaction();
            handleNotifications();
            mBag.isBatchMode = false;
        }
        return _rowsCount;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase _db = null;
        try {
            mBag.isBatchMode = true;
            _db = mDbHelper.getWritableDatabase();
            _db.beginTransaction();
            ContentProviderResult[] _contentProviderResults = super.applyBatch(operations);
            _db.setTransactionSuccessful();
            return _contentProviderResults;
        } finally {
            _db.endTransaction();
            handleNotifications();
            mBag.isBatchMode = false;
        }
    }

    private void handleNotifications() {
        if (mBag.isNotifyStrikes) G4LoaderNotifications.notifyChange(mContext, HgContract.Strikes.CONTENT_URI);
        if (mBag.isNotifyStrikes_vw) G4LoaderNotifications.notifyChange(mContext, HgContract.Strikes_vw.CONTENT_URI);
        if (mBag.isNotifyCompanies) G4LoaderNotifications.notifyChange(mContext, HgContract.Companies.CONTENT_URI);
        if (mBag.isNotifyCompanies_vw) G4LoaderNotifications.notifyChange(mContext, HgContract.Companies_vw.CONTENT_URI);
    }

    class BatchBag {
        public boolean isBatchMode = false;
        public boolean isNotifyStrikes = false;
        public boolean isNotifyStrikes_vw = false;
        public boolean isNotifyCompanies = false;
        public boolean isNotifyCompanies_vw = false;
    }

}
