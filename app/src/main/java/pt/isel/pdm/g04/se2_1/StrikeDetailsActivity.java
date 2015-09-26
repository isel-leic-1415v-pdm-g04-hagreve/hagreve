package pt.isel.pdm.g04.se2_1;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pt.isel.pdm.g04.se2_1.helpers.G4Defs;
import pt.isel.pdm.g04.se2_1.helpers.G4Log;
import pt.isel.pdm.g04.se2_1.helpers.G4Util;
import pt.isel.pdm.g04.se2_1.provider.HgContract;
import pt.isel.pdm.g04.se2_1.serverside.bags.Logo;

public class StrikeDetailsActivity extends Activity {

    public static final String EXTRA_URI = "uri";

    private TextView mDateFromTextView, mDateToTextView, mCompanyTextView, mCancelledTextView,
            mDescriptionTextView;
    private ImageView mCompanyBannerImageView;
    private String mDateFrom, mDateTo, mCompanyName, mSourceLink, mDescription;
    private Bitmap mCompanyBanner;
    private boolean mIsCancelled;

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

    // region Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strike_details);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCompanyBannerImageView = (ImageView) findViewById(R.id.companyBanner);
        mDateFromTextView = (TextView) findViewById(R.id.datesFromData);
        mDateToTextView = (TextView) findViewById(R.id.datesToData);
        mCompanyTextView = (TextView) findViewById(R.id.companyData);
        mCancelledTextView = (TextView) findViewById(R.id.cancelledData);
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionData);

        Intent _intent = getIntent();
        Uri _uri = Uri.parse(_intent.getStringExtra(EXTRA_URI));
        Cursor _cursor = getContentResolver().query(_uri, HgContract.StrikesDetails_vw.PROJECTION_ALL, null, null, null);
        if (_cursor.moveToFirst()) {
            try {
                mDateFrom = _cursor.getString(_cursor.getColumnIndex(HgContract.StrikesDetails_vw.DATE_FROM));
                mDateTo = _cursor.getString(_cursor.getColumnIndex(HgContract.StrikesDetails_vw.DATE_TO));
                mCompanyName = _cursor.getString(_cursor.getColumnIndex(HgContract.StrikesDetails_vw.COMPANY));
                mIsCancelled = _cursor.getInt(_cursor.getColumnIndex(HgContract.StrikesDetails_vw.CANCELLED)) != 0;
                mSourceLink = _cursor.getString(_cursor.getColumnIndex(HgContract.StrikesDetails_vw.SOURCE_LINK));
                mDescription = _cursor.getString(_cursor.getColumnIndex(HgContract.StrikesDetails_vw.DESCRIPTION));
                mCompanyBanner = Logo.toBitmap(_cursor.getBlob(_cursor.getColumnIndex(HgContract.StrikesDetails_vw.BANNER)));

                mDateFromTextView.setText(G4Util.dateFormat(mDateFrom, G4Defs.DATE_8_STRING_FORMAT, G4Defs.DAYMONTH_5_STRING_FORMAT));
                mDateToTextView.setText(G4Util.dateFormat(mDateTo, G4Defs.DATE_8_STRING_FORMAT, G4Defs.DAYMONTH_5_STRING_FORMAT));
                mCompanyTextView.setText(mCompanyName);
                mCancelledTextView.setText(mIsCancelled ? getString(R.string.am_msg_cancelled) : "");
                mDescriptionTextView.setText(mDescription);
                mCompanyBannerImageView.setImageBitmap(mCompanyBanner);
//                G4ImageResources.setStrikeBanner(mCompanyName, mCompanyBannerImageView);
            } catch (ParseException e) {
                Toast.makeText(this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
                G4Log.e("ParseException occurred");
                e.printStackTrace();
            }
        }
        _cursor.close();
    }
    // endregion

    // region Behaviour
    public void browserButtonClicked(View obj) {
        Intent _intent = new Intent(Intent.ACTION_VIEW);
        _intent.setData(Uri.parse(mSourceLink));
        startActivity(_intent);
    }

    public void calendarButtonClicked(View obj) {
        DateFormat df = new SimpleDateFormat(G4Defs.DATETIME_14_STRING_FORMAT);
        Intent _intent;
        try {
            _intent = new Intent(Intent.ACTION_INSERT)
                    .setData(Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, df.parse(mDateFrom).getTime())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, df.parse(mDateTo).getTime())
                    .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                    .putExtra(Events.TITLE, String.format("%s %s", getText(R.string.txt_strike_at), mCompanyName))
                    .putExtra(Events.DESCRIPTION, mDescription)
                    .putExtra(Events.EVENT_LOCATION, "")
                    .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_FREE)
                    .putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE)
                    .putExtra(Events.SELF_ATTENDEE_STATUS, Events.STATUS_TENTATIVE)
                    .putExtra(Events.HAS_ALARM, 0)
                    .putExtra(Events.VISIBLE, 0);
            startActivity(_intent);
        } catch (ParseException e) {
            Toast.makeText(this, R.string.t_internal_err, Toast.LENGTH_SHORT).show();
            G4Log.e("ParseException occurred");
            e.printStackTrace();
        }
    }
    // endregion
}
