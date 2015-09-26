package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.serverside.bags.Submitter;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public class G4JsonConverterSubmitters extends G4JsonConverterTemplate<Submitter> {
    public G4JsonConverterSubmitters(Context ctx) {
        super(ctx);
    }

    // region Abstract Methods Implementation

    @Override
    protected Submitter toItem(JSONObject jsonObject) throws JSONException {
        return Submitter.builder()
                .first_name(jsonObject.getString("first_name"))
                .last_name(jsonObject.getString("last_name"))
                .build();
    }

    @Override
    public Submitter refactor(Submitter item, Collection<Submitter> items) {
        return item;
    }

    // endregion Abstract Methods Implementation

}
