package pt.isel.pdm.g04.se2_1.clientside.bags;

import pt.isel.pdm.g04.se2_1.serverside.bags.HasId;

/**
 * Project SE2-1, created on 2015/04/18.
 */
public class Choice implements HasId {

    private int value;

    public Choice(long value) {
        this((int) value);
    }

    public Choice(int value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return value;
    }
}
