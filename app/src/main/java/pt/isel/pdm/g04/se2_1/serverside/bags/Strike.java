package pt.isel.pdm.g04.se2_1.serverside.bags;

import android.content.Context;
import android.database.Cursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import pt.isel.pdm.g04.se2_1.R;
import pt.isel.pdm.g04.se2_1.clientside.CsCompanies;
import pt.isel.pdm.g04.se2_1.helpers.G4Defs;
import pt.isel.pdm.g04.se2_1.provider.HgDbSchema;

/**
 * Project SE2-1, created on 2015/03/18.
 */
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
    public final Date start_date, end_date; // Datas para o início e fim da greve ("2011-11-30 20:25:23");
    public final String source_link; // URL da fonte dos dados ("http://example.com");
    public final boolean all_day; // true se a greve for de dias inteiros (true)
    public final boolean canceled; // true se a greve tiver sido cancelada (false);
    public final Submitter submitter; // Descreve o autor do aviso.

    public Strike(Context ctx, int id, String description, Date start_date, Date end_date,
                  String source_link, boolean all_day, boolean canceled, Submitter submitter,
                  Company company) {
        this.id = id;
        this.description = description;
        this.start_date = start_date;
        this.end_date = end_date;
        this.source_link = source_link;
        this.all_day = all_day;
        this.canceled = canceled;
        this.submitter = submitter;
        this.company = company;

        SimpleDateFormat _dateFormat = new SimpleDateFormat(G4Defs.MONTHDAY_4_STRING_FORMAT, Locale.getDefault());
        SimpleDateFormat _monthsDateFormat = new SimpleDateFormat(G4Defs.MONTH_3_STRING_FORMAT, Locale.getDefault());
        SimpleDateFormat _daysDateFormat = new SimpleDateFormat(G4Defs.DAY_2_STRING_FORMAT, Locale.getDefault());
        String _strikeFrom = _dateFormat.format(start_date);
        String _strikeTo = _dateFormat.format(end_date);

        put(LBL_COMPANY, company.name);
        put(LBL_DATES, _strikeFrom + (_strikeFrom.equals(_strikeTo) ? "" : " - " + _strikeTo));
        put(LBL_MONTH_FROM, _monthsDateFormat.format(start_date));
        put(LBL_DAY_FROM, _daysDateFormat.format(start_date));
        if (_strikeFrom.equals(_strikeTo)) {
            put(LBL_MONTH_TO, "");
            put(LBL_DAY_TO, "");
        } else {
            put(LBL_MONTH_TO, _monthsDateFormat.format(end_date));
            put(LBL_DAY_TO, _daysDateFormat.format(end_date));
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
        int _colId, _colCompany, _colDescription, _colSource_link, _colStart_date, _colEnd_date,
                _colAll_day, _colCancelled, _colSubmitter;

        _colId = cursor.getColumnIndex(HgDbSchema.COL_ID);
        _colCompany = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_COMPANY);
        _colDescription = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_DESCRIPTION);
        _colSource_link = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_SOURCE_LINK);
        _colStart_date = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_DATE_FROM);
        _colEnd_date = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_DATE_TO);
        _colAll_day = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_ALL_DAY);
        _colCancelled = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_CANCELLED);
//        _colSubmitter = cursor.getColumnIndex(HgDbSchema.Strikes_vw.COL_SUBMITTER);

        Strike result = null;
        DateFormat dateFormat = new SimpleDateFormat(G4Defs.DATETIME_14_STRING_FORMAT, Locale.getDefault());
        int _id = cursor.getInt(_colId);
        Company _company = new CsCompanies(ctx).load_cbg(Integer.parseInt(cursor.getString(_colCompany)));
        String _description = cursor.getString(_colDescription);
        String _source_link = cursor.getString(_colSource_link);
        Date _start_date = dateFormat.parse(cursor.getString(_colStart_date));
        Date _end_date = dateFormat.parse(cursor.getString(_colEnd_date));
        boolean _all_day = cursor.getInt(_colAll_day) > 0;
        boolean _cancelled = cursor.getInt(_colCancelled) > 0;
        Submitter _submitter = null;

        result = builder()
                .id(_id).company(_company).description(_description).source_link(_source_link)
                .start_date(_start_date).end_date(_end_date).all_day(_all_day).cancelled(_cancelled)
                .submitter(_submitter).build(ctx);
        return result;
    }

    // endregion Comparable

    // region Build from Cursor

    @Override
    public String toString() {
        SimpleDateFormat _dateFormat = new SimpleDateFormat("MM-dd", Locale.getDefault());
        String strikeFrom = _dateFormat.format(start_date);
        String strikeTo = _dateFormat.format(end_date);
        return strikeFrom + (strikeFrom.equals(strikeTo) ? "" : " -> " + strikeTo) + " -> " +
                company.name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Strike another) {
        if (canceled != another.canceled) return canceled ? 1 : -1;
        if (id != another.id) return id - another.id;
        if (all_day != another.all_day) return all_day ? 1 : -1;
        int _descriptionCompare = description.compareTo(another.description);
        if (_descriptionCompare != 0) return _descriptionCompare;
        int _end_dateCompare = end_date.compareTo(another.end_date);
        if (_end_dateCompare != 0) return _end_dateCompare;
        int _source_linkCompare = source_link.compareTo(another.source_link);
        if (_source_linkCompare != 0) return _source_linkCompare;
        int _start_dateCompare = start_date.compareTo(another.start_date);
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
