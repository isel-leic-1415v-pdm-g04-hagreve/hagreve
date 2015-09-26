package pt.isel.pdm.g04.se2_1.helpers.jsonconversion;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

import pt.isel.pdm.g04.se2_1.serverside.bags.Company;

/**
 * Project SE2-1, created on 2015/03/19.
 */
public class G4JsonConverterCompanies extends G4JsonConverterTemplate<Company> {
    public G4JsonConverterCompanies(Context ctx) {
        super(ctx);
    }

    // region Abstract Methods Implementation

    @Override
    protected Company toItem(JSONObject jsonObject) throws JSONException {
        return Company.builder()
                .id(jsonObject.getInt("id"))
                .name(jsonObject.getString("name"))
                .build();
    }

    @Override
    public Company refactor(Company item, Collection<Company> items) {
        for (Company company : items)
            if (company.name.equals(item.name)) return Company.builder()
                    .id(item.id)
                    .name(item.name + " (" + String.valueOf(item.id) + ")")
                    .build();
        return item;
    }



    // endregion Abstract Methods Implementation

}
