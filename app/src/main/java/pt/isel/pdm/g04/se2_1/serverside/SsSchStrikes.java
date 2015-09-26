package pt.isel.pdm.g04.se2_1.serverside;

import android.content.Context;

import pt.isel.pdm.g04.se2_1.helpers.G4Http;

/**
 * Project SE2-1, created on 2015/03/20.
 */
public class SsSchStrikes extends SsStrikes {

    public SsSchStrikes(Context ctx) {
        super(ctx);
    }

    @Override
    protected String getCommand() {
        return G4Http.STRIKES;
    }

}
