package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import java.util.Collection;

public class JsonConverterIds extends JsonConverterWrapper<Integer> {
    public JsonConverterIds(Context ctx) {
        super(ctx);
    }

    @Override
    public Collection<Integer> toCollection(String jsonString) {
        throw new UnsupportedOperationException();
    }

}
