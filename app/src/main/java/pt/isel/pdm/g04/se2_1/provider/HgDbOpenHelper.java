package pt.isel.pdm.g04.se2_1.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of Joao Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */
public class HgDbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = HgDbSchema.DB_NAME;
    private static final int DB_VERSION = HgDbSchema.DB_VERSION;

    public HgDbOpenHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteDb(db);
        createDb(db);
    }

    private void deleteDb(SQLiteDatabase db) {
        db.execSQL(HgDbSchema.UnnotifiedStrikes_vw.DDL_DROP_VIEW);
        db.execSQL(HgDbSchema.Companies_vw.DDL_DROP_VIEW);
        db.execSQL(HgDbSchema.StrikesDetails_vw.DDL_DROP_VIEW);
        db.execSQL(HgDbSchema.Strikes_vw.DDL_DROP_VIEW);
        db.execSQL(HgDbSchema.Notifications.DDL_DROP_TABLE);
        db.execSQL(HgDbSchema.Strikes.DDL_DROP_TABLE);
        db.execSQL(HgDbSchema.Timeouts.DDL_DROP_TABLE);
        db.execSQL(HgDbSchema.Choices.DDL_DROP_TABLE);
        db.execSQL(HgDbSchema.Companies.DDL_DROP_TABLE);
        db.execSQL(HgDbSchema.Logos.DDL_DROP_TABLE);
        db.execSQL(HgDbSchema.Submitters.DDL_DROP_UNIQUE_INDEX);
        db.execSQL(HgDbSchema.Submitters.DDL_DROP_TABLE);
    }

    private void createDb(SQLiteDatabase db) {
        db.execSQL(HgDbSchema.Logos.DDL_CREATE_TABLE);
        db.execSQL(HgDbSchema.Companies.DDL_CREATE_TABLE);
        db.execSQL(HgDbSchema.Choices.DDL_CREATE_TABLE);
        db.execSQL(HgDbSchema.Timeouts.DDL_CREATE_TABLE);
        db.execSQL(HgDbSchema.Submitters.DDL_CREATE_TABLE);
        db.execSQL(HgDbSchema.Submitters.DDL_CREATE_UNIQUE_INDEX);
        db.execSQL(HgDbSchema.Strikes.DDL_CREATE_TABLE);
        db.execSQL(HgDbSchema.Notifications.DDL_CREATE_TABLE);
        db.execSQL(HgDbSchema.Strikes_vw.DDL_CREATE_VIEW);
        db.execSQL(HgDbSchema.StrikesDetails_vw.DDL_CREATE_VIEW);
        db.execSQL(HgDbSchema.UnnotifiedStrikes_vw.DDL_CREATE_VIEW);
        db.execSQL(HgDbSchema.Companies_vw.DDL_CREATE_VIEW);
    }

}
