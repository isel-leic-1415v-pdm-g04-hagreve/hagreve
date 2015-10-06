package pt.isel.pdm.g04.se2_1.serverside.bags;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import pt.isel.pdm.g04.se2_1.R;
import pt.isel.pdm.g04.se2_1.clientside.CsCompanies;
import pt.isel.pdm.g04.se2_1.helpers.HgDefs;
import pt.isel.pdm.g04.se2_1.provider.HgDbSchema;

public class Strike extends HashMap<String, String> implements HasId, Comparable<Strike> {

    public final static String LBL_COMPANY = "company";
    public final static String LBL_DATES = "dates";
    public static final String LBL_MONTH_FROM = "month_from";
    public static final String LBL_DAY_FROM = "day_from";
    public static final String LBL_MONTH_TO = "month_to";
    public static final String LBL_DAY_TO = "day_to";
    public static final String LBL_CANCELLED = "cancelled";
    public static final int STRIKE_VW = 0;
    public static final int UN_STRIKE_VW = 1;

    public final int id;
    public final Company company; // Descreve a empresa afectada pela greve.
    public final String description; // Uma descrição da greve ("This is a strike description.");
    public final Date startDate, endDate; // Datas para o início e fim da greve ("2011-11-30 20:25:23");
    public final String sourceLink; // URL da fonte dos dados ("http://example.com");
    public final boolean allDay; // true se a greve for de dias inteiros (true)
    public final boolean canceled; // true se a greve tiver sido cancelada (false);
    public final Submitter submitter; // Descreve o autor do aviso.

    public Strike(Context ctx, int id, String description, Date startDate, Date endDate,
                  String sourceLink, boolean allDay, boolean canceled, Submitter submitter,
                  Company company) {
        this.id = id;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sourceLink = sourceLink;
        this.allDay = allDay;
        this.canceled = canceled;
        this.submitter = submitter;
        this.company = company;

        SimpleDateFormat dateFormat = new SimpleDateFormat(HgDefs.MONTHDAY_4_STRING_FORMAT, Locale.getDefault());
        SimpleDateFormat monthsDateFormat = new SimpleDateFormat(HgDefs.MONTH_3_STRING_FORMAT, Locale.getDefault());
        SimpleDateFormat daysDateFormat = new SimpleDateFormat(HgDefs.DAY_2_STRING_FORMAT, Locale.getDefault());
        String strikeFrom = dateFormat.format(startDate);
        String strikeTo = dateFormat.format(endDate);

        put(LBL_COMPANY, company.name);
        put(LBL_DATES, strikeFrom + (strikeFrom.equals(strikeTo) ? "" : " - " + strikeTo));
        put(LBL_MONTH_FROM, monthsDateFormat.format(startDate));
        put(LBL_DAY_FROM, daysDateFormat.format(startDate));
        if (strikeFrom.equals(strikeTo)) {
            put(LBL_MONTH_TO, "");
            put(LBL_DAY_TO, "");
        } else {
            put(LBL_MONTH_TO, monthsDateFormat.format(endDate));
            put(LBL_DAY_TO, daysDateFormat.format(endDate));
        }
        put(LBL_CANCELLED, canceled ? ctx.getString(R.string.am_msg_cancelled) : "");
    }

    public Strike(Context ctx, Builder builder) {
        this(ctx, builder.mId, builder.mDescription, builder.mStart_date, builder.mEnd_date,
                builder.mSource_link, builder.mAll_day, builder.mCanceled,
                builder.mSubmitter, builder.mCompany);
    }

    // region HasId

    public static Builder builder() {
        return new Builder();
    }

    // endregion HasId

    // region Comparable

    public static Strike build(Context ctx, Cursor cursor) throws ParseException {
        int colId, colCompany, colDescription, colSource_link, colStartDate, colEndDate,
                colAllDay, colCancelled, colSubmitter;

        colId = cursor.getColumnIndex(HgDbSchema.COL_ID);
        colCompany = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_COMPANY);
        colDescription = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_DESCRIPTION);
        colSource_link = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_SOURCE_LINK);
        colStartDate = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_DATE_FROM);
        colEndDate = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_DATE_TO);
        colAllDay = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_ALL_DAY);
        colCancelled = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_CANCELLED);
//        colSubmitter = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_SUBMITTER);

        Strike result = null;
        DateFormat dateFormat = new SimpleDateFormat(HgDefs.DATETIME_14_STRING_FORMAT, Locale.getDefault());
        int id = cursor.getInt(colId);
        Company company = new CsCompanies(ctx).load_cbg(Integer.parseInt(cursor.getString(colCompany)));
        String description = cursor.getString(colDescription);
        String source_link = cursor.getString(colSource_link);
        Date startDate = dateFormat.parse(cursor.getString(colStartDate));
        Date endDate = dateFormat.parse(cursor.getString(colEndDate));
        boolean allDay = cursor.getInt(colAllDay) > 0;
        boolean cancelled = cursor.getInt(colCancelled) > 0;
        Submitter submitter = null;

        return builder()
                .id(id).company(company).description(description).source_link(source_link)
                .start_date(startDate).end_date(endDate).all_day(allDay).cancelled(cancelled)
                .submitter(submitter).build(ctx);
    }

    // endregion Comparable

    // region Build from Cursor

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(HgDefs.MONTHDAY_4_STRING_FORMAT, Locale.getDefault());
        String strikeStart = dateFormat.format(startDate);
        String strikeEnd = dateFormat.format(endDate);
        return strikeStart + (strikeStart.equals(strikeEnd) ? "" : " -> " + strikeEnd) + " -> " +
                company.name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int compareTo(@NonNull Strike another) {
        if (canceled != another.canceled) return canceled ? 1 : -1;
        if (id != another.id) return id - another.id;
        if (allDay != another.allDay) return allDay ? 1 : -1;
        int _descriptionCompare = description.compareTo(another.description);
        if (_descriptionCompare != 0) return _descriptionCompare;
        int _end_dateCompare = endDate.compareTo(another.endDate);
        if (_end_dateCompare != 0) return _end_dateCompare;
        int _source_linkCompare = sourceLink.compareTo(another.sourceLink);
        if (_source_linkCompare != 0) return _source_linkCompare;
        int _start_dateCompare = startDate.compareTo(another.startDate);
        if (_start_dateCompare != 0) return _start_dateCompare;
        int _companyCompare = company.compareTo(another.company);
        if (_companyCompare != 0) return _companyCompare;
        int _submitterCompare = submitter == null ? 0 : submitter.compareTo(another.submitter);
        if (_submitterCompare != 0) return _submitterCompare;
        return 0;
    }

    // endregion Build from Cursor

    // region Builder

    public static class Builder {
        private int mId;
        private String mDescription;
        private Date mStart_date, mEnd_date;
        private String mSource_link;
        private boolean mAll_day;
        private boolean mCanceled;
        private Submitter mSubmitter;
        private Company mCompany;

        public Builder id(int id) {
            this.mId = id;
            return this;
        }

        public Builder description(String description) {
            this.mDescription = description;
            return this;
        }

        public Builder start_date(Date start_date) {
            this.mStart_date = start_date;
            return this;
        }

        public Builder end_date(Date end_date) {
            this.mEnd_date = end_date;
            return this;
        }

        public Builder source_link(String source_link) {
            this.mSource_link = source_link;
            return this;
        }

        public Builder all_day(boolean all_day) {
            this.mAll_day = all_day;
            return this;
        }

        public Builder cancelled(boolean canceled) {
            this.mCanceled = canceled;
            return this;
        }

        public Builder submitter(Submitter submitter) {
            this.mSubmitter = submitter;
            return this;
        }

        public Builder company(Company company) {
            this.mCompany = company;
            return this;
        }

        public Strike build(Context ctx) {
            return new Strike(ctx, this);
        }
    }

    // endregion Builder

}
