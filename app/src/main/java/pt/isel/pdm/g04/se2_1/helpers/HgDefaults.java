package pt.isel.pdm.g04.se2_1.helpers;

import android.view.View;

public interface HgDefaults {

    // region Sharedpreferences
    String SHARED_PREFERENCES = "PDM_SE2-1";
    // endregion Sharedpreferences

    // region Service configuration
    String SCHEMA = "http";
    String HOSTNAME = "isel-leic-1415v-pdm-g04-hagreve.github.io"; //"hagreve.com";
    int PORT = 80;

    int PRE_NOTIFICATION = 4;
    boolean PRE_NOTIFICATION_RANGE = true;
    boolean DAY_NOTIFICATION = true;
    int NOTIFICATION_FREQUENCY = 24;
    boolean NOTIFY_ALWAYS = false;
    // endregion Service configuration

    // region Timeout
    long TIMEOUT = 5 * 60 * 1000;
    // endregion Timeout

    // region Choices Selected Message
    String CHOICES_SEL = "choices_selected";
    String CHOICES_SEL_MSG = "set_view_status";
    int DISPLAY = View.VISIBLE;
    int HIDE = View.GONE;

    String CHOICES_UPD = "choices_updated";
    String CHOICES_UPD_MSG = "choices_values";
    // endregion Choices Selected Message

    // region Battery
    int MIN_BATTERY_WORKING_LEVEL = 16; // Out of 100
    // endregion Battery

    // region Exit
    boolean TAP_TWICE_TO_EXIT = false;
    // endregion
}
