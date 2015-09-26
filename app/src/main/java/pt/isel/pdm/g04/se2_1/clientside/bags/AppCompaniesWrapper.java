package pt.isel.pdm.g04.se2_1.clientside.bags;

import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.g04.se2_1.helpers.G4IHasId;
import pt.isel.pdm.g04.se2_1.serverside.bags.Wrapper;

/**
 * Project SE2-1, created on 2015/03/24.
 */
public class AppCompaniesWrapper extends Wrapper<G4IHasId> {

    public AppCompaniesWrapper(List<G4IHasId> items, boolean isException) {
        super(items, isException);
    }

    public AppCompaniesWrapper(List<G4IHasId> items) {
        this(items, false);
    }

    public AppCompaniesWrapper() {
        super(new ArrayList<G4IHasId>(), true);
    }
}
