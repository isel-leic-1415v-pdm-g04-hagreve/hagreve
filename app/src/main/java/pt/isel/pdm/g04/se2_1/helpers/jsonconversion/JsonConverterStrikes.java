package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.LinkedList;

import pt.isel.pdm.g04.se2_1.helpers.HgDefs;
import pt.isel.pdm.g04.se2_1.serverside.bags.Strike;
import pt.isel.pdm.g04.se2_1.serverside.bags.StrikeLight;

public class JsonConverterStrikes extends JsonConverterWrapper<Strike> {

    public JsonConverterStrikes(Context ctx) {
        super(ctx);
    }

    @Override
    public Collection<Strike> toCollection(String jsonString) {
        Collection<Strike> strikes = new LinkedList<>();
        Gson gson = new GsonBuilder()
                .setDateFormat(HgDefs.DATETIME_12_STRING_FORMAT)
                .create();
        Collection<StrikeLight> strikesLight = gson
                .fromJson(jsonString, new TypeToken<Collection<StrikeLight>>() {
                }.getType());
        for (StrikeLight s : strikesLight)
            strikes.add(Strike.builder().id(s.id).company(s.company).description(s.description)
                    .start_date(s.start_date).end_date(s.end_date).all_day(s.all_day)
                    .source_link(s.source_link).cancelled(s.canceled)
                    .submitter(s.submitter).build(ctx));
        return strikes;
    }

}
