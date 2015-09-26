package pt.isel.pdm.g04.se2_1.serverside;

import android.content.Context;

import org.json.JSONException;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.R;
import pt.isel.pdm.g04.se2_1.helpers.G4Log;
import pt.isel.pdm.g04.se2_1.helpers.jsonconversion.G4JsonConverterStrikes;
import pt.isel.pdm.g04.se2_1.serverside.bags.Strike;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public abstract class SsStrikes extends SsTemplate<Strike> {

    public SsStrikes(Context ctx) {
        super(ctx);
    }

    @Override
    protected String getBaseUrl() {
        return defaultGetBaseUrl();
    }

    @Override
    public Collection<Strike> parseJson() {
        try {
            return new G4JsonConverterStrikes(ctx).toCollection(mJson);
        } catch (JSONException e) {
            alertIOException(R.string.t_srv_unreachable);
            G4Log.e(e.getMessage());
        }
        return null;
    }

    public Collection<Strike> parseJson(String jsonString) {
        try {
            return new G4JsonConverterStrikes(ctx).toCollection(jsonString);
        } catch (JSONException e) {
            alertIOException(R.string.t_srv_unreachable);
            G4Log.e(e.getMessage());
        }
        return null;
    }

}
