package pt.isel.pdm.g04.se2_1.helpers;

/**
 * Project se2-1, created on 2015/05/05.
 */
public interface G4Defs {

    // region Time
    long MILLISEC = 1;
    long SEC = 1000 * MILLISEC;
    long MIN = 60 * SEC;
    long HR = 60 * MIN;
    long DAY = 24 * HR;
    long WEEK = 7 * DAY;
    // endregion Time

    // region Date
    String DATETIME_14_STRING_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String DATETIME_12_STRING_FORMAT = "yy-MM-dd HH:mm:ss";
    String DATETIME_10_STRING_FORMAT = "yy-MM-dd HH:mm";
    String DATE_8_STRING_FORMAT = "yyyy-MM-dd";
    String MONTH_3_STRING_FORMAT = "MMM";
    String DAYMONTH_5_STRING_FORMAT = "dd MMM";
    String MONTHDAY_5_STRING_FORMAT = "MMM-dd";
    String MONTHDAY_4_STRING_FORMAT = "MM-dd";
    String DAY_2_STRING_FORMAT = "dd";
    String WDAY_STRING_FORMAT = "EEE";
    // region Date

}
