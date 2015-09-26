package pt.isel.pdm.g04.se2_1.serverside;

import android.content.Context;

import org.json.JSONException;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.R;
import pt.isel.pdm.g04.se2_1.helpers.G4Http;
import pt.isel.pdm.g04.se2_1.helpers.G4Log;
import pt.isel.pdm.g04.se2_1.helpers.jsonconversion.G4JsonConverterCompanies;
import pt.isel.pdm.g04.se2_1.serverside.bags.Company;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public class SsCompanies extends SsTemplate<Company> {

    public SsCompanies(Context ctx) {
        super(ctx);
    }

    @Override
    protected String getBaseUrl() {
        return defaultGetBaseUrl();
    }

    @Override
    protected String getCommand() {
        return G4Http.COMPANIES;
    }

    @Override
    public Collection<Company> parseJson() {
        Collection<Company> _companies = null;
        try {
            _companies = new G4JsonConverterCompanies(ctx).toCollection(mJson);
        } catch (JSONException e) {
            alertIOException(R.string.t_srv_unreachable);
            G4Log.e(e.getMessage());
        }
        return _companies;
    }
}
