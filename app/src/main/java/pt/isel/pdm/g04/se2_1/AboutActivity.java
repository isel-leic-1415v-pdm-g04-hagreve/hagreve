package pt.isel.pdm.g04.se2_1;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {

    // region Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView _licenseTv = (TextView) findViewById(R.id.aa_license_tv);
        _licenseTv.setMovementMethod(new ScrollingMovementMethod());
    }
    // endregion

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

}
