package pt.isel.pdm.g04.se2_1.serverside;

import java.util.Collection;

/**
 * Project SE2-1, created on 2015/04/17.
 */
public interface ParseJsonFlow<T> {
    Collection<T> parseJson();
}
