package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.serverside.bags.Submitter;

public class JsonConverterSubmitters extends JsonConverterWrapper<Submitter> {

    public JsonConverterSubmitters(Context ctx) {
        super(ctx);
    }

    @Override
    public Collection<Submitter> toCollection(String jsonString) {
        throw new UnsupportedOperationException();
    }

}
