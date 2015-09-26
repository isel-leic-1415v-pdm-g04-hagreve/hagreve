package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import java.util.Collection;

public abstract class JsonConverterWrapper<T> {
    protected final Context ctx;

    protected JsonConverterWrapper(Context ctx) {
        this.ctx = ctx;
    }

    public abstract Collection<T> toCollection(String jsonString);
}
