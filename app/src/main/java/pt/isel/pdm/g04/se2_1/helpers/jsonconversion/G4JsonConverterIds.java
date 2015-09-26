package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public class G4JsonConverterIds extends G4JsonConverterTemplate<Integer> {
    public G4JsonConverterIds(Context ctx) {
        super(ctx);
    }

    // region Abstract Methods Implementation

    @Override
    protected Integer toItem(JSONObject jsonObject) throws JSONException {
        return Integer.parseInt(jsonObject.toString());
    }

    @Override
    public Integer refactor(Integer item, Collection<Integer> items) {
        return item;
    }

    // endregion Abstract Methods Implementation

}
