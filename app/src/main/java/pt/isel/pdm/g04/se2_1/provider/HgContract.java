package pt.isel.pdm.g04.se2_1.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Acknowledgements:
 * The code organization to implement the content provider was based on the ideas of
 * Wolfram Rittmeyer exposed in his blog [Grokking Android - Getting Down to the Nitty Gritty of Android Development]
 * and in the lectures of João Trindade who referred Rittmeyer\'s work and commented it suggesting improvements.
 * Blog is at https://www.grokkingandroid.com/android-tutorial-content-provider-basics
 */
public interface HgContract {

    String AUTHORITY = "pt.isel.pdm.g04.se2_1.hgprovider";
    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    String SELECTION_BY_ID = BaseColumns._ID + " = ? ";
    String MEDIA_BASE_SUBTYPE = "/vnd.g04.se2_1.";

    interface Strikes extends BaseColumns {
        String RESOURCE = "strikes";
        String COMPANY = "company";
        String DATE_FROM = "date_from";
        String DATE_TO = "date_to";
        String ALL_DAY = "all_day";
        String CANCELLED = "cancelled";
        String DESCRIPTION = "description";
        String SOURCE_LINK = "source_link";
        String SUBMITTER = "submitter";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, COMPANY, DATE_FROM, DATE_TO, ALL_DAY, CANCELLED,
                DESCRIPTION, SOURCE_LINK
//                , SUBMITTER_ID
        };
        String DEFAULT_SORT_ORDER = DATE_FROM + "ASC, " + COMPANY + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Strikes_vw extends Strikes, Companies {
        String RESOURCE = "strikes_vw";
        String COMPANY = "company";
        String SUBMITTER_ID = "submitter";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, COMPANY, DATE_FROM, DATE_TO, ALL_DAY,
                CANCELLED, DESCRIPTION, SOURCE_LINK, LOGO
//                , SUBMITTER_ID
        };
        String DEFAULT_SORT_ORDER = DATE_FROM + "ASC, " + COMPANY + " COLLATE NOCASE ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface StrikesDetails_vw extends Strikes, Companies, Logos {
        String RESOURCE = "strikes_details_vw";
        String COMPANY = "company";
        String SUBMITTER_ID = "submitter";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, COMPANY, DATE_FROM, DATE_TO, ALL_DAY,
                CANCELLED, DESCRIPTION, SOURCE_LINK, BANNER
//                , SUBMITTER_ID
        };
        String DEFAULT_SORT_ORDER = DATE_FROM + "ASC, " + COMPANY + " COLLATE NOCASE ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Companies extends BaseColumns {
        String RESOURCE = "companies";
        String NAME = "name";
        String LOGO = "logo";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, NAME};
        String DEFAULT_SORT_ORDER = NAME + " COLLATE NOCASE ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Companies_vw extends Companies, Choices {
        String RESOURCE = "companies_vw";
        String CHOICE = "choice";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, NAME, CHOICE};
        String DEFAULT_SORT_ORDER = NAME + " COLLATE NOCASE ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Timeouts extends BaseColumns {
        String RESOURCE = "timeouts";
        String SOURCE = "source";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String TIMEOUT = "timeout";
        String[] PROJECTION_ALL = {_ID, SOURCE, TIMEOUT};
        String DEFAULT_SORT_ORDER = SOURCE + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Submitters extends BaseColumns {
        String RESOURCE = "submitters";
        String FIRST_NAME = "first_name";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String LAST_NAME = "last_name";
        String[] PROJECTION_ALL = {_ID, FIRST_NAME, LAST_NAME};
        String DEFAULT_SORT_ORDER = FIRST_NAME + " COLLATE NOCASE ASC, " + LAST_NAME + " COLLATE NOCASE ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Choices extends BaseColumns {
        String RESOURCE = "choices";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID};
        String DEFAULT_SORT_ORDER = _ID + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Logos extends BaseColumns {
        String RESOURCE = "logos";
        String SOURCEID = "source_id";
        String LOGO = "logo";
        String BANNER = "banner";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, LOGO, BANNER};
        String DEFAULT_SORT_ORDER = _ID + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface Notifications extends BaseColumns {
        String RESOURCE = "notifications";
        String TIMESTAMP = "timestamp";
        String TYPE = "type";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = {_ID, TIMESTAMP};
        String DEFAULT_SORT_ORDER = _ID + " ASC";
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
    }

    interface UnnotifiedStrikes_vw extends Notifications, Strikes {
        String RESOURCE = "unnotified_strikes_vw";
        Uri CONTENT_URI = Uri.withAppendedPath(HgContract.CONTENT_URI, RESOURCE);
        String[] PROJECTION_ALL = Strikes.PROJECTION_ALL;
        String DEFAULT_SORT_ORDER = Strikes.DEFAULT_SORT_ORDER;
        String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
        String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + MEDIA_BASE_SUBTYPE + RESOURCE;
}   }
