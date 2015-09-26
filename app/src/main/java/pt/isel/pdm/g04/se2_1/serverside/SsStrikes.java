package pt.isel.pdm.g04.se2_1.serverside;

import android.content.Context;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.helpers.jsonconversion.JsonConverterStrikes;
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
        return new JsonConverterStrikes(ctx).toCollection(mJson);
    }

    public Collection<Strike> parseJson(String jsonString) {
        return new JsonConverterStrikes(ctx).toCollection(jsonString);
    }

}
