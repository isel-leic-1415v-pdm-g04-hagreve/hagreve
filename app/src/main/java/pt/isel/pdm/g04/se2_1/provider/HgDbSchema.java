package pt.isel.pdm.g04.se2_1.provider;

import android.provider.BaseColumns;

/**
 * Project SE2-1, created on 2015/04/13.
 *
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of Joao Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */
public interface HgDbSchema {

    String DB_NAME = "hagreve.db";
    int DB_VERSION = 35;
    String COL_ID = BaseColumns._ID;

    interface Strikes {
        String TBL_NAME = HgContract.Strikes.RESOURCE;
        String COL_COMPANY = HgContract.Strikes.COMPANY;
        String COL_DATE_FROM = HgContract.Strikes.DATE_FROM;
        String COL_DATE_TO = HgContract.Strikes.DATE_TO;
        String COL_ALL_DAY = HgContract.Strikes.ALL_DAY;
        String COL_CANCELLED = HgContract.Strikes.CANCELLED;
        String COL_DESCRIPTION = HgContract.Strikes.DESCRIPTION;
        String COL_SOURCE_LINK = HgContract.Strikes.SOURCE_LINK;
        String COL_SUBMITTER = HgContract.Strikes.SUBMITTER;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY" +
                ", " + COL_COMPANY + " NUMBER NOT NULL" + // COMPANY._ID
                ", " + COL_DATE_FROM + " TEXT NOT NULL" + // see http://www.sqlite.org/lang_datefunc.html
                ", " + COL_DATE_TO + " TEXT" + // see http://www.sqlite.org/lang_datefunc.html
                ", " + COL_ALL_DAY + " NUMBER" + // == 0 ? FALSE : TRUE
                ", " + COL_CANCELLED + " NUMBER" + // == 0 ? FALSE : TRUE
                ", " + COL_DESCRIPTION + " TEXT" +
                ", " + COL_SOURCE_LINK + " TEXT" +
                ", " + COL_SUBMITTER + " NUMBER" + // SUBMITTER_ID._ID
                ", CONSTRAINT fkc_strikes_company FOREIGN KEY(" + COL_COMPANY +
                ") REFERENCES " + Companies.TBL_NAME + "(" + COL_ID + ")" +
//                ", CONSTRAINT fkc_strikes_submitter FOREIGN KEY(" + COL_SUBMITTER_ID +
//                ") REFERENCES " + Submitter.TBL_NAME + "(" + COL_ID + ")" +
                ");";
    }

    interface Strikes_vw extends Strikes, Companies, Choices, Logos {
        String VIEW_NAME = HgContract.Strikes_vw.RESOURCE;
        String COL_STRIKE_ID = HgContract.Strikes._ID;
        String COL_COMPANY = HgContract.Strikes_vw.COMPANY;

        String DDL_DROP_VIEW = "DROP VIEW IF EXISTS " + VIEW_NAME;
        String DDL_CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS SELECT " +
                Strikes.TBL_NAME + "." + COL_ID + " AS " + COL_STRIKE_ID +
                ", " + Companies.TBL_NAME + "." + Companies.COL_NAME + " AS " + COL_COMPANY +
                ", " + Strikes.TBL_NAME + "." + COL_DATE_FROM + " AS " + COL_DATE_FROM +
                ", " + Strikes.TBL_NAME + "." + COL_DATE_TO + " AS " + COL_DATE_TO +
                ", " + Strikes.TBL_NAME + "." + COL_ALL_DAY + " AS " + COL_ALL_DAY +
                ", " + Strikes.TBL_NAME + "." + COL_CANCELLED + " AS " + COL_CANCELLED +
                ", " + Strikes.TBL_NAME + "." + COL_DESCRIPTION + " AS " + COL_DESCRIPTION +
                ", " + Strikes.TBL_NAME + "." + COL_SOURCE_LINK + " AS " + COL_SOURCE_LINK +
                ", " + Logos.TBL_NAME + "." + Logos.COL_LOGO + " AS " + Logos.COL_LOGO +
//                ", " + Submitters.TBL_NAME + "." + COL_ID + " AS " + COL_SUBMITTER_ID +
                " FROM " + Strikes.TBL_NAME + " INNER JOIN " + Companies.TBL_NAME + " ON " +
                Strikes.TBL_NAME + "." + Strikes.COL_COMPANY + " = " + Companies.TBL_NAME + "." + COL_ID +
                " LEFT JOIN " + Choices.TBL_NAME + " ON " +
                Strikes.TBL_NAME + "." + Strikes.COL_COMPANY + " = " + Choices.TBL_NAME + "." + COL_ID +
                " INNER JOIN " + Logos.TBL_NAME + " ON " +
                Companies.TBL_NAME + "." + Companies.COL_LOGO + " = " + Logos.TBL_NAME + "." + COL_ID +
                " WHERE " + Choices.TBL_NAME + "." + COL_ID + " IS NULL" +
                // TODO: Tweak date('now') | date('now', 'start of month')
                " AND date(" + COL_DATE_TO + ") >= date('now')" +
                ";";
    }

    interface StrikesDetails_vw extends Strikes, Companies, Logos {
        String VIEW_NAME = HgContract.StrikesDetails_vw.RESOURCE;
        String COL_STRIKE_ID = HgContract.Strikes._ID;
        String COL_COMPANY = HgContract.StrikesDetails_vw.COMPANY;

        String DDL_DROP_VIEW = "DROP VIEW IF EXISTS " + VIEW_NAME;
        String DDL_CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS SELECT " +
                Strikes.TBL_NAME + "." + COL_ID + " AS " + COL_STRIKE_ID +
                ", " + Companies.TBL_NAME + "." + Companies.COL_NAME + " AS " + COL_COMPANY +
                ", " + Strikes.TBL_NAME + "." + COL_DATE_FROM + " AS " + COL_DATE_FROM +
                ", " + Strikes.TBL_NAME + "." + COL_DATE_TO + " AS " + COL_DATE_TO +
                ", " + Strikes.TBL_NAME + "." + COL_ALL_DAY + " AS " + COL_ALL_DAY +
                ", " + Strikes.TBL_NAME + "." + COL_CANCELLED + " AS " + COL_CANCELLED +
                ", " + Strikes.TBL_NAME + "." + COL_DESCRIPTION + " AS " + COL_DESCRIPTION +
                ", " + Strikes.TBL_NAME + "." + COL_SOURCE_LINK + " AS " + COL_SOURCE_LINK +
                ", " + Logos.TBL_NAME + "." + COL_BANNER + " AS " + COL_BANNER +
//                ", " + Submitters.TBL_NAME + "." + COL_ID + " AS " + COL_SUBMITTER_ID +
                " FROM " + Strikes.TBL_NAME + " INNER JOIN " + Companies.TBL_NAME + " ON " +
                Strikes.TBL_NAME + "." + Strikes.COL_COMPANY + " = " + Companies.TBL_NAME + "." + COL_ID +
                " INNER JOIN " + Logos.TBL_NAME + " ON " +
                Companies.TBL_NAME + "." + Companies.COL_LOGO + " = " + Logos.TBL_NAME + "." + COL_ID +
                ";";
    }

    interface Companies {
        String TBL_NAME = HgContract.Companies.RESOURCE;
        String COL_NAME = HgContract.Companies.NAME;
        String COL_LOGO = HgContract.Companies.LOGO;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY" +
                ", " + COL_NAME + " TEXT UNIQUE " +
                ", " + COL_LOGO + " INTEGER DEFAULT -1" +
                ", CONSTRAINT fkc_companies_logo FOREIGN KEY(" + COL_LOGO +
                ") REFERENCES " + Logos.TBL_NAME + "(" + COL_ID + ")" +
                ");";
    }

    interface Companies_vw extends Companies, Choices {
        String VIEW_NAME = HgContract.Companies_vw.RESOURCE;

        String DDL_DROP_VIEW = "DROP VIEW IF EXISTS " + VIEW_NAME;
        String DDL_CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS SELECT " +
                Companies.TBL_NAME + "." + COL_ID + " AS " + COL_ID +
                ", " + Companies.TBL_NAME + "." + COL_NAME + " AS " + COL_NAME +
                " FROM " + Companies.TBL_NAME +
                " LEFT JOIN " + Choices.TBL_NAME + " ON " +
                Companies.TBL_NAME + "." + COL_ID + " = " + Choices.TBL_NAME + "." + COL_ID +
                ";";
    }

    interface Timeouts {
        String TBL_NAME = HgContract.Timeouts.RESOURCE;
        String COL_SOURCE = HgContract.Timeouts.SOURCE;
        String COL_TIMEOUT = HgContract.Timeouts.TIMEOUT;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + COL_SOURCE + " TEXT UNIQUE" +
                ", " + COL_TIMEOUT + " NUMBER" +
                ");";
    }

    interface Submitters {
        String TBL_NAME = HgContract.Submitters.RESOURCE;
        String COL_FIRST_NAME = HgContract.Submitters.FIRST_NAME;
        String COL_LAST_NAME = HgContract.Submitters.LAST_NAME;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX uc_submitters ON " +
                Submitters.TBL_NAME + "(" + COL_FIRST_NAME + ", " + COL_LAST_NAME + ");";
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                ", " + COL_FIRST_NAME + " TEXT" +
                ", " + COL_LAST_NAME + " TEXT" +
                ");";
        String DDL_DROP_UNIQUE_INDEX = "DROP INDEX IF EXISTS uc_submitters;";
    }

    interface Choices {
        String TBL_NAME = HgContract.Choices.RESOURCE;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY" +
                ", CONSTRAINT fkc_choices_companies FOREIGN KEY(" + COL_ID +
                ") REFERENCES " + Companies.TBL_NAME + "(" + COL_ID + ")" +
                ");";
    }

    interface Logos {
        String TBL_NAME = HgContract.Logos.RESOURCE;
        String COL_SOURCEID = HgContract.Logos.SOURCEID;
        String COL_LOGO = HgContract.Logos.LOGO;
        String COL_BANNER = HgContract.Logos.BANNER;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY " +
                ", " + COL_SOURCEID + " INTEGER " +
                ", " + COL_LOGO + " BLOB NOT NULL" +
                ", " + COL_BANNER + " BLOB NOT NULL" +
                ");";
    }

    interface Notifications {
        String TBL_NAME = HgContract.Notifications.RESOURCE;
        String COL_TIMESTAMP = HgContract.Notifications.TIMESTAMP;
        String COL_TYPE = HgContract.Notifications.TYPE;

        String DDL_DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME;
        String DDL_CREATE_TABLE = "CREATE TABLE " + TBL_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY" +
                ", " + COL_TIMESTAMP + " TEXT DEFAULT CURRENT_TIMESTAMP" + // see http://www.sqlite.org/lang_datefunc.html
                ", " + COL_TYPE + " INTEGER default 0" +
                ", CONSTRAINT fkn_notifications_strikes FOREIGN KEY(" + COL_ID +
                ") REFERENCES " + Strikes.TBL_NAME + "(" + COL_ID + ")" +
                ");";
    }

    interface UnnotifiedStrikes_vw extends Strikes, Notifications {
        String VIEW_NAME = HgContract.UnnotifiedStrikes_vw.RESOURCE;
        String COL_STRIKE_ID = HgContract.UnnotifiedStrikes_vw._ID;

        String DDL_DROP_VIEW = "DROP VIEW IF EXISTS " + VIEW_NAME;
        String DDL_CREATE_VIEW = "CREATE VIEW " + VIEW_NAME + " AS SELECT " +
                Strikes.TBL_NAME + "." + COL_ID + " AS " + COL_STRIKE_ID +
                ", " + Companies.TBL_NAME + "." + COL_ID + " AS " + COL_COMPANY +
                ", " + Strikes.TBL_NAME + "." + COL_DATE_FROM + " AS " + COL_DATE_FROM +
                ", " + Strikes.TBL_NAME + "." + COL_DATE_TO + " AS " + COL_DATE_TO +
                ", " + Strikes.TBL_NAME + "." + COL_ALL_DAY + " AS " + COL_ALL_DAY +
                ", " + Strikes.TBL_NAME + "." + COL_CANCELLED + " AS " + COL_CANCELLED +
                ", " + Strikes.TBL_NAME + "." + COL_DESCRIPTION + " AS " + COL_DESCRIPTION +
                ", " + Strikes.TBL_NAME + "." + COL_SOURCE_LINK + " AS " + COL_SOURCE_LINK +
//                ", " + Submitters.TBL_NAME + "." + COL_ID + " AS " + COL_SUBMITTER_ID +
                ", " + Notifications.TBL_NAME + "." + COL_TYPE + " AS " + COL_TYPE +
                " FROM " + Strikes.TBL_NAME + " INNER JOIN " + Companies.TBL_NAME + " ON " +
                Strikes.TBL_NAME + "." + Strikes.COL_COMPANY + " = " + Companies.TBL_NAME + "." + COL_ID +
                " LEFT JOIN " + Choices.TBL_NAME + " ON " +
                Strikes.TBL_NAME + "." + Strikes.COL_COMPANY + " = " + Choices.TBL_NAME + "." + COL_ID +
                " LEFT JOIN " + Notifications.TBL_NAME + " ON " +
                Strikes.TBL_NAME + "." + COL_ID + " = " + Notifications.TBL_NAME + "." + COL_ID +
                " WHERE " + Choices.TBL_NAME + "." + COL_ID + " IS NULL" +
//                " AND " + Notifications.TBL_NAME + "." + COL_ID + " IS NULL" +
                // TODO: Tweak date('now') | date('now', 'start of month')
                " AND date(" + COL_DATE_FROM + ") >= date('now')" +
                ";";
    }

}
