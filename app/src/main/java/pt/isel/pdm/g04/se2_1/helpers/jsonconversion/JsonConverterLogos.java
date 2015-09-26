package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.serverside.bags.Logo;

public class JsonConverterLogos extends JsonConverterWrapper<Logo> {

    public JsonConverterLogos(Context ctx) {
        super(ctx);
    }

    @Override
    public Collection<Logo> toCollection(String jsonString) {
        return new Gson().fromJson(jsonString, new TypeToken<Collection<Logo>>() {
        }.getType());
    }

}
