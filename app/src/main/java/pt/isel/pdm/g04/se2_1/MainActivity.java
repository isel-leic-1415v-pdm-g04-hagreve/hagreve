package pt.isel.pdm.g04.se2_1;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import pt.isel.pdm.g04.se2_1.android3party.WakefulIntentService;
import pt.isel.pdm.g04.se2_1.clientside.CsChoices;
import pt.isel.pdm.g04.se2_1.helpers.HgDefaults;
import pt.isel.pdm.g04.se2_1.helpers.HgDefs;
import pt.isel.pdm.g04.se2_1.helpers.HgLog;
import pt.isel.pdm.g04.se2_1.provider.HgContract;
import pt.isel.pdm.g04.se2_1.serverside.bags.Logo;

// region Comments

/*
 * Project SE2-1, created on 2015/03/18.
 *
 * Há greve.getAPI()
 *
 * http://hagreve.com/api/v2/toCollection
 * http://hagreve.com/api/v2/strikes
 * http://hagreve.com/api/v2/allstrikes
 *
 * Menu (Persistência):
 *  > Configuração de servidor
 *  > Filter-out: http://hagreve.com/api/v2/toCollection
 * Main activity:
 *  > Próximas greves: http://hagreve.com/api/v2/strikes
 *  > Drilldown
 *  > Browser: sourceLink
 *  > Calendário
 */

// endregion Comments

public class MainActivity extends ActionBarActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    // region Types

    // static
    private static final int STRIKES_LOADER = 0;
    private static final String[] sDataMap;
    private static final int[] sViewsMap;
    private static final int BACKKEY_TIME_INTERVAL = 2 * (int) HgDefs.SEC;

    static {
//        sDataMap = new String[]{HgContract.Strikes_vw.COMPANY, HgContract.Strikes_vw.CANCELLED,
//                HgContract.Strikes_vw.DATE_FROM, HgContract.Strikes_vw.DATE_FROM,
//                HgContract.Strikes_vw.DATE_TO, HgContract.Strikes_vw.DATE_TO, HgContract.Strikes_vw.COMPANY};
        sDataMap = new String[]{HgContract.Strikes_vw.COMPANY, HgContract.Strikes_vw.CANCELLED,
                HgContract.Strikes_vw.DATE_FROM, HgContract.Strikes_vw.DATE_FROM, HgContract.Strikes_vw.DATE_FROM,
                HgContract.Strikes_vw.DATE_TO, HgContract.Strikes_vw.DATE_TO, HgContract.Strikes_vw.LOGO};
        sViewsMap = new int[]{R.id.companyName_data, R.id.cancelled_data,
                R.id.weekdayFrom_data, R.id.dayFrom_data, R.id.monthFrom_data,
                R.id.dayTo_data, R.id.monthTo_data, R.id.list_image};
    }

    // non-static
    private TextView mWarningsTextView;
    private SimpleCursorAdapter mAdapter = null;
    private long timeBackKeyPressed;

    // endregion

    // region Menus
    // region Behaviour
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            int _visibility = intent.getIntExtra(HgDefaults.CHOICES_SEL_MSG, 0);
            mWarningsTextView.setVisibility(
                    _visibility == View.VISIBLE ? View.VISIBLE :
                            _visibility == View.GONE ? View.GONE : View.INVISIBLE);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // endregion Menus

    // region LifeCycle

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_companies:
                startActivity(new Intent(this, CompaniesActivity.class));
                break;
            case R.id.action_refresh:
//                HaGreveServices.startActionGetStrikes(this, true);
                Bundle _intentExtra = new Bundle();
                ArrayList<String> booleans = new ArrayList<>();
                booleans.add("bypass");
                _intentExtra.putStringArrayList("booleansKeys", booleans);
                _intentExtra.putBooleanArray("booleansValues", new boolean[] {true});
                _intentExtra.putString("action", HaGreveServices.ACTION_GET_COMPANIES_AND_STRIKES);
                WakefulIntentService.sendWakefulWork(this, HaGreveServices.class, _intentExtra);
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            default:
                throw new IllegalStateException("This should never have happened...");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new SimpleCursorAdapter(this, R.layout.listview_row, null, sDataMap, sViewsMap, 0);
        mAdapter.setViewBinder(new CustomViewBinder());

        checkOnAppLogo(R.drawable.ic_stat_strike);

        ListView _listView = (ListView) findViewById(R.id.listView);
        _listView.setEmptyView(findViewById(R.id.noDataTextView));
        _listView.setAdapter(mAdapter);
        _listView.setOnItemClickListener(this);

        mWarningsTextView = (TextView) findViewById(R.id.warningsTextView);
        mWarningsTextView.setOnClickListener(new CompaniesClickListener());

        getLoaderManager().initLoader(STRIKES_LOADER, null, this);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(HgDefaults.CHOICES_SEL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CsChoices(this).check();
    }

    // endregion

    // region Presentation

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    // endregion

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case STRIKES_LOADER:
                return new CursorLoader(this, HgContract.Strikes_vw.CONTENT_URI,
                        HgContract.Strikes_vw.PROJECTION_ALL, null, null, null);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent _intent = new Intent(this, StrikeDetailsActivity.class);
        _intent.putExtra(StrikeDetailsActivity.EXTRA_URI,
                Uri.withAppendedPath(HgContract.StrikesDetails_vw.CONTENT_URI, String.valueOf(id)).toString());
        startActivity(_intent);
    }

    @Override
    public void onBackPressed() {
        if (!HgDefaults.TAP_TWICE_TO_EXIT || timeBackKeyPressed + BACKKEY_TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        Toast.makeText(getBaseContext(), getString(R.string.am_msg_tap_twice), Toast.LENGTH_SHORT).show();
        timeBackKeyPressed = System.currentTimeMillis();
    }

    // region internal support methods
    private void checkOnAppLogo(int logoId) {
        Cursor _cursor = getContentResolver().query(HgContract.Logos.CONTENT_URI, null, HgContract.Logos._ID + " = ?", new String[]{"-1"}, null);
        try {
            if (!_cursor.moveToFirst()) {
                Bitmap _bitmap = BitmapFactory.decodeResource(getResources(), logoId);
                Logo _logo = new Logo(-1, -1, "HaGreve");
                ContentValues _contentValues = Logo.toContentValues(_logo, _bitmap, _bitmap);
                getContentResolver().insert(HgContract.Logos.CONTENT_URI, _contentValues);
            }
        } finally {
            _cursor.close();
        }
    }

    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            DateFormat _str = new SimpleDateFormat(HgDefs.DATE_8_STRING_FORMAT, Locale.getDefault());
            DateFormat _mmm = new SimpleDateFormat(HgDefs.MONTH_3_STRING_FORMAT, Locale.getDefault());
            DateFormat _dd = new SimpleDateFormat(HgDefs.DAY_2_STRING_FORMAT, Locale.getDefault());
            DateFormat _wd = new SimpleDateFormat(HgDefs.WDAY_STRING_FORMAT, Locale.getDefault());
            try {
                String _value;
                switch (view.getId()) {
                    case R.id.weekdayFrom_data:
                        _value = cursor.getString(columnIndex);
                        ((TextView) view).setText(_wd.format(_str.parse(_value)));
                        return true;
                    case R.id.dayFrom_data:
                        _value = cursor.getString(columnIndex);
                        ((TextView) view).setText(_dd.format(_str.parse(_value)));
                        return true;
                    case R.id.monthFrom_data:
                        _value = cursor.getString(columnIndex);
                        ((TextView) view).setText(_mmm.format(_str.parse(_value)));
                        return true;
                    case R.id.dayTo_data:
                        _value = cursor.getString(columnIndex);
                        ((TextView) view).setText(
                                _value.substring(0, 10).compareTo(cursor.getString(columnIndex - 1).substring(0, 10)) != 0 ?
                                        _dd.format(_str.parse(_value)) : "");
                        return true;
                    case R.id.monthTo_data:
                        _value = cursor.getString(columnIndex);
                        ((TextView) view).setText(
                                _value.substring(0, 10).compareTo(cursor.getString(columnIndex - 1).substring(0, 10)) != 0 ?
                                        _mmm.format(_str.parse(_value)) : "");
                        return true;
                    case R.id.cancelled_data:
                        ((TextView) view).setText(
                                cursor.getInt(columnIndex) == 0 ? "" : getString(R.string.am_msg_cancelled));
                        return true;
                    case R.id.list_image:
//                        ((ImageView) view).setImageResource(G4ImageResources.getStrikeIcon(cursor.getString(columnIndex)));
                        ((ImageView) view).setImageBitmap(Logo.toBitmap(cursor.getBlob(columnIndex)));
                        return true;

                    default:
                        return false;
                }
            } catch (ParseException e) {
                Toast.makeText(MainActivity.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                HgLog.e("ParseException occurred");
                e.printStackTrace();
            }
            return false;
        }
    }

    // endregion

    private class CompaniesClickListener implements AdapterView.OnClickListener {

        @Override
        public void onClick(View v) {
            startActivity(new Intent(getBaseContext(), CompaniesActivity.class));
        }
    }
    // endregion
}
