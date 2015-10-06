package pt.isel.pdm.g04.se2_1.serverside;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collection;

import pt.isel.pdm.g04.se2_1.helpers.HgHttp;
import pt.isel.pdm.g04.se2_1.helpers.HgLog;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public abstract class SsTemplate<T> implements ParseJsonFlow<T> {

    protected final Context ctx;
    protected String mJson;

    public SsTemplate(Context ctx) {
        this.ctx = ctx;
        mJson = null;
    }

    // region Abstract Methods

    protected abstract String getBaseUrl();

    protected abstract String getCommand();

    public abstract Collection<T> parseJson();

    // endregion Abstract Methods

    public Collection<T> retrieveCollection() throws IOException {
        return retrieveJson().parseJson();
    }

    public ParseJsonFlow<T> retrieveJson() throws IOException {
        return retrieveJson(getBaseUrl(), getCommand());
    }

    public ParseJsonFlow<T> retrieveJson(int id) throws IOException {
        return retrieveJson(getBaseUrl(), getCommand());
    }

    @Override
    public String toString() {
        return mJson;
    }

    // region Private support methods

    private ParseJsonFlow<T> retrieveJson(String schemaAuthority, String command) throws IOException {
        HgLog.i("Acessing server " + schemaAuthority);
        mJson = (new HgHttp(ctx, schemaAuthority, HgHttp.URL_BASE_PATH, command)).getString();
        return this;
    }

    // endregion Private support methods

    // region Protected support methods

    protected final void alertIOException(int message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    protected final void alertIOException(String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    protected String defaultGetBaseUrl() {
        return HgHttp.buildBaseUrl(ctx);
    }

    // endregion Protected support methods

}
