package pt.isel.pdm.g04.se2_1.serverside.bags;

import java.util.Collection;

/**
 * Project SE2-1, created on 2015/03/24.
 */
public abstract class Wrapper<T> {
    public final Collection<T> items;
    public final boolean isException;

    public Wrapper(Collection<T> items, boolean isException) {
        this.items = items;
        this.isException = isException;
    }

    public Wrapper(Collection<T> items) {
        this(items, false);
    }

}
