package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import pt.isel.pdm.g04.se2_1.helpers.G4Defs;
import pt.isel.pdm.g04.se2_1.serverside.bags.Strike;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public class G4JsonConverterStrikes extends G4JsonConverterTemplate<Strike> {
    public G4JsonConverterStrikes(Context ctx) {
        super(ctx);
    }

    @Override
    public Strike refactor(Strike item, Collection<Strike> items) {
        return item;
    }

    // region Abstract Methods Implementation

    @Override
    protected Strike toItem(JSONObject jsonObject) throws JSONException {
        SimpleDateFormat _simpleDateFormat = new SimpleDateFormat(G4Defs.DATETIME_12_STRING_FORMAT);
        // 2015-03-18 06:00:00
        try {
            Strike strike = Strike.builder()
                    .id(jsonObject.getInt("id"))
                    .description(jsonObject.getString("description"))
                    .start_date(_simpleDateFormat.parse(jsonObject.getString("start_date")))
                    .end_date(_simpleDateFormat.parse(jsonObject.getString("end_date")))
                    .source_link(jsonObject.getString("source_link"))
                    .all_day(jsonObject.getBoolean("all_day"))
                    .cancelled(jsonObject.getBoolean("canceled"))
                    .submitter(new G4JsonConverterSubmitters(ctx).toItem(jsonObject.getString("submitter")))
                    .company(new G4JsonConverterCompanies(ctx).toItem(jsonObject.getString("company")))
                    .build(ctx);
            return strike;
        } catch (ParseException e) {
            throw new JSONException(e.getMessage());
        }
    }

    // endregion Abstract Methods Implementation

}
