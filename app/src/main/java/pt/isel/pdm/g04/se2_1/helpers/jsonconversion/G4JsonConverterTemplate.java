package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public abstract class G4JsonConverterTemplate<T> {
    protected final Context ctx;

    protected G4JsonConverterTemplate(Context ctx) {
        this.ctx = ctx;
    }

    // region Abstract Methods

    protected abstract T toItem(JSONObject jsonObject) throws JSONException;

    public abstract T refactor(T item, Collection<T> items);

    // endregion Abstract Methods

    public T toItem(String jsonString) throws JSONException {
        JSONObject _jsonObject = new JSONObject(jsonString);
        return toItem(_jsonObject);
    }

    public Collection<T> toCollection(String jsonString) throws JSONException {
        JSONArray _jsonArray = new JSONArray(jsonString);
        Collection<T> _results = new ArrayList<>();
        for (int i = 0; i < _jsonArray.length(); i++) {
            T _item = toItem((JSONObject) _jsonArray.get(i));
            _item = refactor(_item, _results);
            _results.add(_item);
        }
        return _results;
    }

}
