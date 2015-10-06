package pt.isel.pdm.g04.se2_1.clientside.bags;

import pt.isel.pdm.g04.se2_1.helpers.HgHasId;
import pt.isel.pdm.g04.se2_1.serverside.bags.Company;

/**
 * Project SE2-1, created on 2015/03/23.
 */
public class AppCompany extends Company implements HgHasId {
    private boolean excluded;

    public AppCompany(int id, String name) {
        super(id, name);
        this.excluded = false;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public void setExcluded(boolean excluded) {
        this.excluded = excluded;
    }

    @Override
    public int getId() {
        return super.id;
    }
}
