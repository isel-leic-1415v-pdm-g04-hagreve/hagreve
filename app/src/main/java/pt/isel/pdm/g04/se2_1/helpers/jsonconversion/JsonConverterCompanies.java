package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.serverside.bags.Company;

public class JsonConverterCompanies extends JsonConverterWrapper<Company> {

    public JsonConverterCompanies(Context ctx) {
        super(ctx);
    }

    @Override
    public Collection<Company> toCollection(String jsonString) {
        return new Gson().fromJson(jsonString, new TypeToken<Collection<Company>>() {
        }.getType());
    }

}
