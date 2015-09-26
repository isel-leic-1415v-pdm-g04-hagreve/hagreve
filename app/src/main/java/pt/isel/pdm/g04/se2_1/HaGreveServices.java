package pt.isel.pdm.g04.se2_1;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import pt.isel.pdm.g04.se2_1.android3party.WakefulIntentService;
import pt.isel.pdm.g04.se2_1.clientside.CsCompanies;
import pt.isel.pdm.g04.se2_1.clientside.CsStrikes;
import pt.isel.pdm.g04.se2_1.clientside.CsTemplate;
import pt.isel.pdm.g04.se2_1.helpers.G4Defaults;
import pt.isel.pdm.g04.se2_1.helpers.G4Defs;
import pt.isel.pdm.g04.se2_1.helpers.G4DisplayNotifications;
import pt.isel.pdm.g04.se2_1.helpers.G4Http;
import pt.isel.pdm.g04.se2_1.helpers.G4Log;
import pt.isel.pdm.g04.se2_1.helpers.G4SharedPreferences;
import pt.isel.pdm.g04.se2_1.helpers.G4SyncRequirements;
import pt.isel.pdm.g04.se2_1.helpers.jsonconversion.G4JsonConverterLogos;
import pt.isel.pdm.g04.se2_1.provider.HgContract;
import pt.isel.pdm.g04.se2_1.provider.HgDbSchema;
import pt.isel.pdm.g04.se2_1.serverside.SsCompanies;
import pt.isel.pdm.g04.se2_1.serverside.SsSchStrikes;
import pt.isel.pdm.g04.se2_1.serverside.SsStrikes;
import pt.isel.pdm.g04.se2_1.serverside.SsTemplate;
import pt.isel.pdm.g04.se2_1.serverside.bags.Company;
import pt.isel.pdm.g04.se2_1.serverside.bags.HasId;
import pt.isel.pdm.g04.se2_1.serverside.bags.Logo;
import pt.isel.pdm.g04.se2_1.serverside.bags.Strike;

import static pt.isel.pdm.g04.se2_1.helpers.G4Http.slashAppend;

public class HaGreveServices extends WakefulIntentService {

    //region Types

    public static final String ACTION_GET_COMPANIES_AND_STRIKES = "pt.isel.pdm.g04.se2_1.action.GET_STRIKES";
    public static final String ACTION_GET_COMPANIES = "pt.isel.pdm.g04.se2_1.action.GET_COMPANIES";
    public static final String ACTION_CHECK_STRIKES = "pt.isel.pdm.g04.se2_1.action.CHECK_STRIKES";

    public static final String LOGOS_BASEURL = G4Http.LOGO_SERVER;
    public static final String LOGOS_PATH = "";

    //endregion

    public static final String LOGOS_TIMESTAMP = "logos_read";

    public HaGreveServices() {
        super("HaGreveServices");
    }

    // region External interface

    public static void startActionSynchronizeAndNotify(Context ctx) {
        HaGreveServices.startActionGetStrikes(ctx, false);
        HaGreveServices.startActionCheckStrikesToday(ctx);
        HaGreveServices.startActionCheckStrikesInAdvance(ctx, true);
    }

    public static void startActionGetStrikes(Context ctx) {
        startActionGetStrikes(ctx, false);
    }

    public static void startActionGetStrikes(Context ctx, boolean bypass) {
        Intent _intent = new Intent(ctx, HaGreveServices.class);
        _intent.putExtra("bypass", bypass);
        _intent.setAction(ACTION_GET_COMPANIES_AND_STRIKES);
        ctx.startService(_intent);
    }

    public static void startActionGetCompanies(Context ctx) {
        Intent _intent = new Intent(ctx, HaGreveServices.class);
        _intent.setAction(ACTION_GET_COMPANIES);
        ctx.startService(_intent);
    }

    public static void startActionCheckStrikes(Context ctx, int from, int until) {
        Intent _intent = new Intent(ctx, HaGreveServices.class);
        _intent.putExtra("from", from);
        _intent.putExtra("until", until);
        _intent.setAction(ACTION_CHECK_STRIKES);
        ctx.startService(_intent);
    }

    public static void startActionCheckStrikesInAdvance(Context ctx, boolean fromTomorrow) {
        int _until = G4SharedPreferences.getDefault(ctx).
                getInt(SettingsActivity.SP_PRE_NOTIFICATION, G4Defaults.PRE_NOTIFICATION);
        startActionCheckStrikes(ctx, fromTomorrow ? 1 : _until, _until);
    }

    public static void startActionCheckStrikesToday(Context ctx) {
        boolean _isCheckStrikes = G4SharedPreferences.getDefault(ctx).
                getBoolean(SettingsActivity.SP_DAILY_NOTIFICATION, G4Defaults.DAY_NOTIFICATION);
        if (_isCheckStrikes) startActionCheckStrikes(ctx, 0, 0);
    }

    // endregion External interface

    // region Behaviour

    @Override
    protected void doWakefulWork(Intent intent) {
        G4Log.i("HaGreveServices.onHandleIntent");
        boolean _isBypass = intent.getBooleanExtra("bypass", false);
        boolean _notify = !_isBypass || G4SharedPreferences.getDefault(this)
                .getBoolean(SettingsActivity.SP_NOTIFY_ALWAYS, G4Defaults.NOTIFY_ALWAYS);
        if (intent == null ||
                !G4SyncRequirements.isOk(this, G4SyncRequirements.WIFI &
                        (_isBypass ? ~0 : G4SyncRequirements.BATTERY))) {
            G4Log.i("Server sync blocked: Battery low, no wifi connection... or internal error.");
            return;
        }

        switch (intent.getAction()) {
            case ACTION_GET_COMPANIES_AND_STRIKES:
                G4Log.i("HaGreveServices.onHandleIntent -> ACTION_GET_STRIKES");
//                new HandleActionGetCompanies().doWork();
//                new HandleActionGetStrikes().doWork(_notify);
                handleActionStrikes();
                break;
            case ACTION_GET_COMPANIES:
                G4Log.i("HaGreveServices.onHandleIntent -> ACTION_GET_COMPANIES");
                new HandleActionGetCompanies().doWork();
                break;
            case ACTION_CHECK_STRIKES:
                G4Log.i("HaGreveServices.onHandleIntent -> ACTION_CHECK_STRIKES");
                int _defaultUntil = G4SharedPreferences.getDefaultTag(this,
                        SettingsActivity.SP_PRE_NOTIFICATION, G4Defaults.PRE_NOTIFICATION);
                int _until = intent.getIntExtra("until", _defaultUntil);
                int _defaultFrom = G4SharedPreferences.getDefaultTag(this,
                        SettingsActivity.SP_PRE_NOTIFICATION_TYPE, G4Defaults.PRE_NOTIFICATION_RANGE) ?
                        1 : _until;
                int _from = intent.getIntExtra("from", _defaultFrom);
                new HandleActionCheckStrikes().doWork(_from, _until);
                break;
            default:
                throw new UnsupportedOperationException("Unknown action within the current ctx.");
        }
    }

    // region Get

    // region Old Gets (for All companies)

    private class HandleActionGetStrikes extends HandleAction<Strike, SsStrikes, CsStrikes> {
        @Override
        protected SsStrikes getServerSide(Context ctx) {
            return new SsSchStrikes(ctx);
        }

        @Override
        protected CsStrikes getClientSide(Context ctx) {
            return new CsStrikes(ctx);
        }

        @Override
        protected boolean isAcceptable(Strike item) {
            return item.end_date.compareTo(new Date()) >= 0;
        }

        @Override
        protected String getCsSelection() {
            return HgContract.Strikes.DATE_TO + " >= ?";
        }

        @Override
        protected String[] getCsSelectionArguments() {
            DateFormat df = new SimpleDateFormat(G4Defs.DATETIME_14_STRING_FORMAT);
            return new String[]{df.format(new Date())};
        }
    }

    private class HandleActionGetCompanies extends HandleAction<Company, SsCompanies, CsCompanies> {
        @Override
        protected SsCompanies getServerSide(Context ctx) {
            return new SsCompanies(ctx);
        }

        @Override
        protected CsCompanies getClientSide(Context ctx) {
            return new CsCompanies(ctx);
        }

        @Override
        protected boolean isAcceptable(Company item) {
            return true;
        }

        @Override
        protected String getCsSelection() {
            return null;
        }

        @Override
        protected String[] getCsSelectionArguments() {
            return new String[0];
        }
    }

    private abstract class HandleAction<T extends HasId & Comparable, S extends SsTemplate<T>, C extends CsTemplate<T>> {
        protected abstract S getServerSide(Context ctx);

        protected abstract C getClientSide(Context ctx);

        protected abstract boolean isAcceptable(T item);

        protected abstract String getCsSelection();

        protected abstract String[] getCsSelectionArguments();

        protected boolean doWork() {
            return doWork(false);
        }

        protected boolean doWork(boolean isNotify) {
            boolean _result = false;
            try {
                Collection<T> _ssItems = getServerSide(HaGreveServices.this).retrieveJson().parseJson();
                C _csItems = getClientSide(HaGreveServices.this);
                Map<Integer, T> _referenceMap = new HashMap<>();
                Map<Integer, T> _toDelete = new HashMap<>();
                Collection<T> _items = _csItems.load_cbg(getCsSelection(), getCsSelectionArguments());
                for (T item : _items) {
                    _referenceMap.put(item.getId(), item);
                    _toDelete.put(item.getId(), item);
                }
                Collection<T> _toReplace = new LinkedList<>();
                Collection<T> _toInsert = new LinkedList<>();
                for (T ssItem : _ssItems) {
                    if (isAcceptable(ssItem)) {
                        int _id = ssItem.getId();
                        if (_referenceMap.containsKey(_id)) {
                            if (_referenceMap.get(_id).compareTo(ssItem) != 0) _toReplace.add(ssItem);
                            _toDelete.remove(_id);
                        } else {
                            T newItem = _csItems.refactor(ssItem, _items);
                            _toInsert.add(newItem);
                        }
                    }
                }
                int _toDeleteCount = _toDelete.size();
                int _toReplaceCount = _toReplace.size();
                int _toInsertCount = _toInsert.size();
                G4Log.i("to delete: " + _toDeleteCount +
                        "; to replace: " + _toReplaceCount +
                        "; to insert: " + _toInsertCount +
                        ".");
                boolean _isToDelete = _toDeleteCount > 0;
                boolean _isToReplace = _toReplaceCount > 0;
                boolean _isToInsert = _toInsertCount > 0;
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                Set<Integer> _blockedCompanies = getBlockedCompanies();
                if (_isToDelete) _csItems.prepareDeleteBatch(_toDelete.values(), ops);
                if (_isToReplace) {
                    _csItems.prepareUpdateBatch(_toReplace, ops);
                    if (isNotify) {
                        Collection<Strike> _filteredStrikes = getFilteredStrikes(_toReplace, _blockedCompanies);
                        G4DisplayNotifications.notify(HaGreveServices.this, _filteredStrikes,
                                G4DisplayNotifications.STRIKE_UPDATED);
                    }
                }
                if (_isToInsert) {
                    _csItems.prepareInsertBatch(_toInsert, ops);
                    if (isNotify){
                        G4DisplayNotifications.notify(HaGreveServices.this, (Collection<Strike>)_toInsert,
                                G4DisplayNotifications.NEW_STRIKE);
                    }
                }
                try {
                    getContentResolver().applyBatch(HgContract.AUTHORITY, ops);
                } catch (RemoteException | OperationApplicationException e) {
                    Toast.makeText(HaGreveServices.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                    G4Log.e("RemoteException occurred");
                    e.printStackTrace();
                }
                _result = _isToDelete || _isToReplace || _isToInsert;
            } catch (IOException | ParseException e) {
                Toast.makeText(HaGreveServices.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                G4Log.e("IOException or ParseException occurred");
                e.printStackTrace();
            }
            return _result;
        }
    }

    // endregion

    // region New Gets (for In use companies)

    private void handleActionLogos(boolean forceUpdate) {
        Set <Integer> ids = new HashSet<>();
        Cursor _cursor = getContentResolver().query(HgContract.Companies.CONTENT_URI, null, null, null, null);
        if (_cursor.moveToFirst()) {
            int _colId = _cursor.getColumnIndex(HgContract.Companies._ID);
            do {
                ids.add(_cursor.getInt(_colId));
            } while (_cursor.moveToNext());
        }
        handleActionLogos(ids, forceUpdate);
    }

    private void handleActionLogos(Set<Integer> ids, boolean forceUpdate) {
        try {
            String _jsonString = new G4Http(this, LOGOS_BASEURL, LOGOS_PATH, null).getString();
            JSONObject _jsonObject = new JSONObject(_jsonString);
            Date _serverLastUpdate = toDate(_jsonObject.getString("last_update"));
            Date _logosUpdateTimestamp = getTimestamp(LOGOS_TIMESTAMP);
            if (! forceUpdate && _logosUpdateTimestamp.compareTo(_serverLastUpdate) >= 0) return;
            String _jsonArrayString = _jsonObject.getString("map");
            Collection<Logo> _logos = new G4JsonConverterLogos(this).toCollection(_jsonArrayString);
            for (Logo logo : _logos) {
                if (ids.contains(logo.id)) {
                    String _logoJsonString = new G4Http(this, LOGOS_BASEURL, LOGOS_PATH, logo.path_link).getString();
                    JSONObject _logoJsonObject = new JSONObject(_logoJsonString);
                    Date _logoServerLastUpdate = toDate(_logoJsonObject.getString("last_update"));
                    if (! forceUpdate && _logosUpdateTimestamp.compareTo(_logoServerLastUpdate) >= 0) continue;
                    JSONArray _files = _logoJsonObject.getJSONArray("files");
                    String _logoFile = "", _bannerFile = "";
                    for (int i = 0; i < _files.length(); i ++) {
                        JSONObject _file = (JSONObject) _files.get(i);
                        if (_logoFile.equals("") && _file.getString("type").equals("logo")) {
                            _logoFile = _file.getString("name");
                            continue;
                        }
                        if (_bannerFile.equals("") && _file.getString("type").equals("banner")) {
                            _bannerFile = _file.getString("name");
                            continue;
                        }
                    }
                    Bitmap _logo = new G4Http(this, LOGOS_BASEURL, LOGOS_PATH, slashAppend(logo.path_link, _logoFile)).getBitMap();
                    if (_logo != null) {
                        Bitmap _banner = new G4Http(this, LOGOS_BASEURL, LOGOS_PATH, slashAppend(logo.path_link, _bannerFile)).getBitMap();
                        if (_banner != null) {
                            getContentResolver().delete(HgContract.Logos.CONTENT_URI,
                                    HgContract.Logos._ID + " = ?",
                                    new String[]{String.valueOf(logo.id)});
                            ContentValues _contentValues = Logo.toContentValues(logo, _logo, _banner);
                            getContentResolver().insert(HgContract.Logos.CONTENT_URI, _contentValues);
                            _contentValues = new ContentValues();
                            _contentValues.put(HgContract.Companies.LOGO, logo.id);
                            getContentResolver().update(HgContract.Companies.CONTENT_URI, _contentValues,
                                    HgContract.Companies._ID + " = ?", new String[]{String.valueOf(logo.id)});
                        }
                    }
                }
            }
            setTimestamp(LOGOS_TIMESTAMP);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleActionCompanies(Map<Integer, Company> _ssItems) throws ParseException {
        CsCompanies _csHandler = new CsCompanies(this);
        Collection<Company> _csItems = _csHandler.load_cbg(null, null);
        Collection<Company> _itemsToReplace = new LinkedList<>(), _itemsToInsert = new LinkedList<>();
        Map<Integer, Company> _csMap = new HashMap<>();
        for (Company item : _csItems) _csMap.put(item.id, item);
        for (Company item : _ssItems.values()) {
            int _id = item.id;
            if (!_csMap.containsKey(_id)) {
                Company newItem = _csHandler.refactor(item, _csItems);
                _itemsToInsert.add(newItem);
                continue;
            }
            if (_csMap.get(_id).compareTo(item) != 0) _itemsToReplace.add(item);
        }
        boolean _isToReplace = _itemsToReplace.size() > 0;
        boolean _isToInsert = _itemsToInsert.size() > 0;
        if (_isToReplace && _isToInsert == false) return;
        ArrayList<ContentProviderOperation> _ops = new ArrayList<>();
        if (_isToReplace) _csHandler.prepareUpdateBatch(_itemsToReplace, _ops);
        if (_isToInsert) _csHandler.prepareInsertBatch(_itemsToInsert, _ops);
        try {
            getContentResolver().applyBatch(HgContract.AUTHORITY, _ops);
            Set<Integer> _currentCompanies = new HashSet<>();
            for (Integer id : _csMap.keySet()) _currentCompanies.add(id);
            for (Company company : _itemsToInsert) _currentCompanies.add(company.id);
            handleActionLogos(_currentCompanies, false);
        } catch (RemoteException | OperationApplicationException e) {
            Toast.makeText(HaGreveServices.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
            G4Log.e("RemoteException occurred");
            e.printStackTrace();
        }
    }

    private boolean handleActionStrikes() {
        try {
            Collection<Strike> _ssItems = new SsSchStrikes(this).retrieveJson().parseJson();
            if (_ssItems == null) return false;
            Collection<Strike> _itemsToReplace = new LinkedList<>(), _itemsToInsert = new LinkedList<>();
            CsStrikes _csHandler = new CsStrikes(this);
            Map<Integer, Strike> _csMap = new HashMap<>(), _itemsToDelete = new HashMap<>();
            DateFormat df = new SimpleDateFormat(G4Defs.DATETIME_14_STRING_FORMAT);
            Collection<Strike> _csItems = _csHandler.load_cbg(HgContract.Strikes.DATE_TO + " >= ?", new String[]{df.format(new Date())});
            Set<Integer> _blockedCompanies = getBlockedCompanies();
            for (Strike item : _csItems) {
                _csMap.put(item.getId(), item);
                _itemsToDelete.put(item.getId(), item);
            }
            Map<Integer, Company> _ssCompanies = new HashMap<>();
            Date _now = new Date();
            for (Strike item : _ssItems) {
                int _id = item.company.id;
                if (_blockedCompanies.contains(_id)) continue;
                if (!_ssCompanies.containsKey(_id)) _ssCompanies.put(_id, item.company);
                if (item.end_date.compareTo(_now) >= 0) {
                    _id = item.id;
                    if (!_csMap.containsKey(_id)) {
                        _itemsToInsert.add(item);
                        continue;
                    }
                    if (_csMap.get(_id).compareTo(item) != 0) _itemsToReplace.add(item);
                    _itemsToDelete.remove(_id);
                }
            }

            if (_ssCompanies.size() > 0) handleActionCompanies(_ssCompanies);

            boolean _isToDelete = _itemsToDelete.size() > 0;
            boolean _isToReplace = _itemsToReplace.size() > 0;
            boolean _isToInsert = _itemsToInsert.size() > 0;

            if (_isToInsert) handleActionLogos(true);
            else if (_ssCompanies.size() == 0) handleActionLogos(false);

            if (_isToDelete && _isToReplace && _isToInsert == false) return false;

            ArrayList<ContentProviderOperation> _strikesOps = new ArrayList<>();
            if (_isToDelete) _csHandler.prepareDeleteBatch(_itemsToDelete.values(), _strikesOps);
            if (_isToReplace) {
                _csHandler.prepareUpdateBatch(_itemsToReplace, _strikesOps);
                G4DisplayNotifications.notify(HaGreveServices.this, _itemsToReplace, G4DisplayNotifications.STRIKE_UPDATED);
            }
            if (_isToInsert) {
                _csHandler.prepareInsertBatch(_itemsToInsert, _strikesOps);
                G4DisplayNotifications.notify(HaGreveServices.this, _itemsToInsert, G4DisplayNotifications.NEW_STRIKE);
            }
            try {
                getContentResolver().applyBatch(HgContract.AUTHORITY, _strikesOps);
            } catch (RemoteException | OperationApplicationException e) {
                Toast.makeText(HaGreveServices.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                G4Log.e("RemoteException occurred");
                e.printStackTrace();
            }
        } catch (IOException | ParseException e) {
            Toast.makeText(HaGreveServices.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
            G4Log.e("IOException or ParseException occurred");
            e.printStackTrace();
        }
        return true;
    }

    // endregion

    private Set<Integer> getBlockedCompanies() {
        Set<Integer> _choices = new HashSet<>();
        Cursor _cursor = getContentResolver().query(HgContract.Choices.CONTENT_URI, HgContract.Choices.PROJECTION_ALL, null, null, null);
        if (_cursor.moveToFirst()) {
            int _colIdIdx = _cursor.getColumnIndex(HgDbSchema.COL_ID);
            do {
                _choices.add(_cursor.getInt(_colIdIdx));
            } while (_cursor.moveToNext());
        }
        _cursor.close();
        return _choices;
    }

    private static <T> Collection<Strike> getFilteredStrikes(Collection<T> items, Set<Integer> filter) {
        Collection<Strike> _filteredStrikes = new LinkedList<>();
        if (items == null || items.size() == 0 || !(items.iterator().next() instanceof Strike)) return _filteredStrikes;
        for (Strike strike : ( Collection<Strike>)items) if (!filter.contains(strike.company.id)) _filteredStrikes.add(strike);
        return _filteredStrikes;
    }

    // endregion

    // region Check

    private static final int STRIKE_COMING = 0;
    private static final int STRIKE_TODAY = 1;

    private class HandleActionCheckStrikes {
        protected boolean doWork(int countFrom, int countUntil) {
            if (countFrom > countUntil + 1) throw new IllegalArgumentException();
            DateFormat df = new SimpleDateFormat(G4Defs.DATETIME_14_STRING_FORMAT, Locale.US);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, countFrom);
            Date _begin = cal.getTime();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.add(Calendar.DAY_OF_MONTH, countUntil - countFrom);
            Date _end = cal.getTime();
            int _notificationType = countFrom == 0 && countUntil == 0 ? STRIKE_TODAY : STRIKE_COMING;
            boolean _result = false;
            Collection<Strike> _notifiedStrikes = new LinkedList<>();
            Cursor _cursor = null;
            try {
                _cursor = getContentResolver().query(HgContract.UnnotifiedStrikes_vw.CONTENT_URI,
                        HgContract.UnnotifiedStrikes_vw.PROJECTION_ALL,
                        HgDbSchema.UnnotifiedStrikes_vw.COL_DATE_FROM + " <= ? AND " +
                                HgDbSchema.UnnotifiedStrikes_vw.COL_DATE_TO + " >= ? AND (" +
                                HgDbSchema.UnnotifiedStrikes_vw.COL_TYPE + " IS NULL OR " +
                                HgDbSchema.UnnotifiedStrikes_vw.COL_TYPE + " > ? )",
                        new String[]{df.format(_end), df.format(_begin), String.valueOf(_notificationType)},
                        null);
                _result = _cursor.getCount() > 0;
                if (!_result) G4Log.i("-No Strikes until " + df.format(_end));
                while (_cursor.moveToNext()) {
                    Strike _strike = null;
                    _strike = Strike.build(HaGreveServices.this, _cursor);
                    int _icon = _notificationType == STRIKE_COMING ?
                            G4DisplayNotifications.STRIKE_COMING :
                            G4DisplayNotifications.STRIKE_TODAY;
                    G4DisplayNotifications.notify(HaGreveServices.this, _strike, _icon);
                    _notifiedStrikes.add(_strike);
                    G4Log.i("Strike on " + _strike.company.name + " stating at " + df.format(_strike.start_date));
                }
            } catch (ParseException e) {
                Toast.makeText(HaGreveServices.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                G4Log.e("ParseException occurred");
                e.printStackTrace();
                return false;
            } finally {
                if (_cursor != null) _cursor.close();
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(HgDbSchema.Notifications.COL_TIMESTAMP, df.format(new Date()));
            contentValues.put(HgDbSchema.Notifications.COL_TYPE, _notificationType);
            ContentResolver contentResolver = getContentResolver();
            for (Strike strike : _notifiedStrikes) {
                contentValues.put(HgDbSchema.COL_ID, strike.getId());
                contentResolver.insert(HgContract.Notifications.CONTENT_URI, contentValues);
            }
            return _result;
        }
    }

    // endregion

    // endregion

    // region internal support methods

    private void setTimestamp(String tag) {
        G4SharedPreferences.getDefault(this).edit().putLong(tag, new Date().getTime()).commit();
    }

    private Date getTimestamp(String tag) {
        return new Date(G4SharedPreferences.getDefault(this).getLong(tag, 0));
    }

    private Date toDate(String dateString) {
        DateFormat _df = new SimpleDateFormat(G4Defs.DATETIME_14_STRING_FORMAT);
        Date _date;
        try {
            _date = _df.parse(dateString);
        } catch (ParseException e) {
            _date = new Date(0);
        }
        return _date;
    }

    private int compareTimestamp(String tag, String dateString) {
        return compareTimestamp(tag, toDate(dateString));
    }

    private int compareTimestamp(String tag, Date date) {
        long _storedDateLong = G4SharedPreferences.getDefault(this).getLong(tag, 0);
        Date _storedDate = new Date(_storedDateLong);
        int _comparison = _storedDate.compareTo(date);
        return _comparison;
//        return new Date(G4SharedPreferences.getDefault(this).getLong(tag, 0)).compareTo(date);
    }

    // endregion
}
