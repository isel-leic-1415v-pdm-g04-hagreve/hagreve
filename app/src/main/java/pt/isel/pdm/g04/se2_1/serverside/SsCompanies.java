package pt.isel.pdm.g04.se2_1.serverside;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

import pt.isel.pdm.g04.se2_1.helpers.G4Defs;
import pt.isel.pdm.g04.se2_1.helpers.G4Http;
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
        Gson gson = new GsonBuilder()
                .setDateFormat(G4Defs.DATETIME_12_STRING_FORMAT)
                .create();
        Type collectionType = new TypeToken<Collection<Company>>() {
        }.getType();
        return gson.fromJson(mJson, collectionType);
    }
}
