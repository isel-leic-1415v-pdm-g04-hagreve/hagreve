package pt.isel.pdm.g04.se2_1;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import pt.isel.pdm.g04.se2_1.clientside.CsCompanies;
import pt.isel.pdm.g04.se2_1.clientside.CsStrikes;
import pt.isel.pdm.g04.se2_1.helpers.G4Defaults;
import pt.isel.pdm.g04.se2_1.helpers.G4SharedPreferences;

public class SettingsActivity extends ActionBarActivity {

    // region Types
    public final static String SP_SCHEMA = "protocol";
    public final static String SP_HOSTNAME = "hostname";
    public final static String SP_PORT = "port";
    public final static String SP_PRE_NOTIFICATION = "pre_notification";
    public final static String SP_PRE_NOTIFICATION_TYPE = "pre_notification_type";
    public final static String SP_DAILY_NOTIFICATION = "day_notification";
    public final static String SP_SYNCHRONIZATION_FREQUENCY = "notification_period";
    public final static String SP_NOTIFY_ALWAYS = "notification_always";

    // non-static
    private SharedPreferences mSharedPreferences;
    private EditText mHostnameEditText, mPortNumberEditText, mPreNotificationEditText, mSynchFrequencyEditText;
    private String mHostName;
    private int mPortNumber, mPreNotification, mSynchFrequency;
    private boolean mDailyNotification, mNotifyAlways;
    private Switch mDailyNotificationCheckBox, mNotifyAlwaysCheckBox;
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
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = G4SharedPreferences.getDefault(this);
        mHostName = mSharedPreferences.getString(SP_HOSTNAME, G4Defaults.HOSTNAME);
        mPortNumber = mSharedPreferences.getInt(SP_PORT, G4Defaults.PORT);
        mPreNotification = mSharedPreferences.getInt(SP_PRE_NOTIFICATION, G4Defaults.PRE_NOTIFICATION);
        mDailyNotification = mSharedPreferences.getBoolean(SP_DAILY_NOTIFICATION, G4Defaults.DAY_NOTIFICATION);
        mSynchFrequency = mSharedPreferences.getInt(SP_SYNCHRONIZATION_FREQUENCY, G4Defaults.NOTIFICATION_FREQUENCY);
        mNotifyAlways = mSharedPreferences.getBoolean(SP_NOTIFY_ALWAYS, G4Defaults.NOTIFY_ALWAYS);

        mHostnameEditText = (EditText) findViewById(R.id.editTextHostname);
        mHostnameEditText.setText(mHostName);
        mPortNumberEditText = (EditText) findViewById(R.id.editTextPort);
        mPortNumberEditText.setText(String.valueOf(mPortNumber));
        mPreNotificationEditText = (EditText) findViewById(R.id.editTextAlertBefore);
        mPreNotificationEditText.setText(String.valueOf(mPreNotification));
        mDailyNotificationCheckBox = (Switch) findViewById(R.id.switchDailyNotification);
        mDailyNotificationCheckBox.setChecked(mDailyNotification);
        mSynchFrequencyEditText = (EditText) findViewById(R.id.editTextSynchFrequency);
        mSynchFrequencyEditText.setText(String.valueOf(mSynchFrequency));
        mNotifyAlwaysCheckBox = (Switch) findViewById(R.id.switchNotifyAlways);
        mNotifyAlwaysCheckBox.setChecked(mNotifyAlways);
    }

    @Override
    protected void onStop() {
        super.onStop();
        new AsyncTask<OnStopBag, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(OnStopBag... onStopBags) {
                OnStopBag _bag = onStopBags[0];
                if (_bag.isConfigurationChanged) {
                    mSharedPreferences.edit()
                            .putString(SP_HOSTNAME, _bag.hostName)
                            .putInt(SP_PORT, _bag.portNumber)
                            .putInt(SP_PRE_NOTIFICATION, _bag.preNotification)
                            .putBoolean(SP_DAILY_NOTIFICATION, _bag.dailyNotification)
                            .putInt(SP_SYNCHRONIZATION_FREQUENCY, _bag.synchronizationFrequency)
                            .putBoolean(SP_NOTIFY_ALWAYS, _bag.notifyAlways)
                            .commit();
                }
                if (_bag.isResyncRequired) {
                    new CsStrikes(SettingsActivity.this).deleteAll_cbg();
                    new CsCompanies(SettingsActivity.this).deleteAll_cbg();
                    mSharedPreferences.edit().remove(HaGreveServices.LOGOS_TIMESTAMP).commit();
                    HaGreveServices.startActionGetStrikes(SettingsActivity.this, true);
                }
                if (mSynchFrequency != Integer.parseInt(String.valueOf(mSynchFrequencyEditText.getText()))) {
                    // TODO: Ver se � para resolver ou remover
                }
                return null;
            }
        }.execute(new OnStopBag());
    }

    private class OnStopBag {
        public final String hostName = String.valueOf(mHostnameEditText.getText());
        public final int portNumber = Integer.parseInt(String.valueOf(mPortNumberEditText.getText()));
        public final int preNotification = Integer.parseInt(String.valueOf(mPreNotificationEditText.getText()));
        public final boolean dailyNotification = mDailyNotificationCheckBox.isChecked();
        public final int synchronizationFrequency = Integer.parseInt(String.valueOf(mSynchFrequencyEditText.getText()));
        public final boolean notifyAlways = mNotifyAlwaysCheckBox.isChecked();
        public final boolean isConfigurationChanged = mPortNumber != portNumber ||
                ! mHostName.equals(hostName) || mPreNotification != preNotification ||
                mDailyNotification != dailyNotification || mSynchFrequency != synchronizationFrequency ||
                mNotifyAlways != notifyAlways;
        public final boolean isResyncRequired = mPortNumber != portNumber || ! mHostName.equals(hostName);
    }

    // endregion LifeCycle

    // region Behaviour

    public void settingsResetButtonClick(View obj) {
        mHostnameEditText.setText(G4Defaults.HOSTNAME);
        mPortNumberEditText.setText(String.valueOf(G4Defaults.PORT));
    }

    // endregion Behaviour

}
