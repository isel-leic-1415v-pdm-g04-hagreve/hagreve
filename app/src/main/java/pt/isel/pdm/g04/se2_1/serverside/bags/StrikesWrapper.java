package pt.isel.pdm.g04.se2_1.serverside.bags;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Project SE2-1, created on 2015/03/24.
 */
public class StrikesWrapper extends Wrapper<Strike> {

    public final int blockedCompaniesCount;

    public StrikesWrapper(Collection<Strike> strikes, int blockedCompaniesCount, boolean isException) {
        super(strikes, isException);
        this.blockedCompaniesCount = blockedCompaniesCount;
    }

    public StrikesWrapper(Collection<Strike> strikes, int blockedCompaniesCount) {
        this(strikes, blockedCompaniesCount, false);
    }

    public StrikesWrapper(Collection<Strike> strikes) {
        this(strikes, 0);
    }

    public StrikesWrapper() {
        this(new ArrayList<Strike>(), 0, true);
    }
}
