package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

import pt.isel.pdm.g04.se2_1.serverside.bags.Logo;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public class G4JsonConverterLogos {
    private Context mContext;

    public G4JsonConverterLogos(Context ctx) {
        mContext = ctx;
    }

    protected Logo toItem(JSONObject jsonObject) throws JSONException {
        return new Logo(
                jsonObject.getInt("id"),
                jsonObject.getInt("path_link"),
                jsonObject.getString("name_hint"));
    }

    public Collection<Logo> toCollection(String jsonString) throws JSONException {
        JSONArray _jsonArray = new JSONArray(jsonString);
        Collection<Logo> _results = new ArrayList<>();
        for (int i = 0; i < _jsonArray.length(); i ++) {
            Logo _item = toItem((JSONObject) _jsonArray.get(i));
            _results.add(_item);
        }
        return _results;
    }

}
