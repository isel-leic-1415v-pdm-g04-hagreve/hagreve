package pt.isel.pdm.g04.se2_1;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Arrays;

import pt.isel.pdm.g04.se2_1.clientside.CsChoices;
import pt.isel.pdm.g04.se2_1.clientside.bags.Choice;
import pt.isel.pdm.g04.se2_1.helpers.HgDefaults;
import pt.isel.pdm.g04.se2_1.helpers.HgLog;
import pt.isel.pdm.g04.se2_1.provider.HgContract;

public class CompaniesActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // region Types

    // static
    private static final int COMPANIES_LOADER = 1;
    private static final int sListView = android.R.layout.simple_list_item_multiple_choice;
    private static final String[] sDataMap;
    private static final int[] sViewsMap;

    static {
        sDataMap = new String[]{HgContract.Companies.NAME};
        sViewsMap = new int[]{android.R.id.text1};
    }

    // non-static
    private ListView mListView = null;
    private CursorAdapter mAdapter = null;
    private BroadcastReceiver mMessageReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

    // endregion Types

    // region Menus

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                throw new IllegalStateException("This should never have happened...");
        }
    }

    // endregion

    // region LifeCycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies);

        mAdapter = new SimpleCursorAdapter(this, sListView, null, sDataMap, sViewsMap, 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.companiesListView);
        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setEmptyView(findViewById(R.id.noDataTextView));
        mListView.setOnItemClickListener(new OnItemClickListener());
        getLoaderManager().initLoader(COMPANIES_LOADER, null, this);
        mMessageReceiver = new CompaniesBroadcastReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mMessageReceiver, new IntentFilter(HgDefaults.CHOICES_UPD));
    }

    @Override
    protected void onDestroy() {
        mLocalBroadcastManager.unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    // endregion LifeCycle

    // region Behaviour

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {switch (id) {
            case COMPANIES_LOADER:
                return new CursorLoader(this, HgContract.Companies.CONTENT_URI,
                        HgContract.Companies.PROJECTION_ALL, null, null, null);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        new CsChoices(this).loadAll();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class CompaniesBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            int[] _choices = intent.getIntArrayExtra(HgDefaults.CHOICES_UPD_MSG);
            for (int i = 0; i < mListView.getCount(); i++) {
                Cursor _cursor = (Cursor) mListView.getItemAtPosition(i);
                int _colId = _cursor.getColumnIndex(HgContract.Companies._ID);
                Arrays.sort(_choices);
                if (Arrays.binarySearch(_choices, _cursor.getInt(_colId)) >= 0) {
                    mListView.setItemChecked(i, true);
                }
            }
        }
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            long[] _checkedItemIds = ((ListView) parent).getCheckedItemIds();
            boolean _isItemChecked = Arrays.binarySearch(_checkedItemIds, id) >= 0;
            Choice _choice = new Choice(id);
            if (_isItemChecked) {
                new AsyncTask<Choice, Void, Void>() {
                    @Override
                    protected Void doInBackground(Choice... params) {
                        try {
                            new CsChoices(CompaniesActivity.this).insert_cbg(params[0]);
                        } catch (ParseException e) {
                            Toast.makeText(CompaniesActivity.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                            HgLog.e("ParseException occurred");
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(_choice);
            } else {
                new AsyncTask<Choice, Void, Void>() {
                    @Override
                    protected Void doInBackground(Choice... params) {
                        try {
                            new CsChoices(CompaniesActivity.this).delete_cbg(params[0]);
                        } catch (ParseException e) {
                            Toast.makeText(CompaniesActivity.this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                            HgLog.e("ParseException occurred");
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(_choice);
            }
        }
    }

    // endregion Behaviour

}
