package com.github.pvasilyev.solr.birthday.impl;

import java.lang.invoke.MethodHandles;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.github.pvasilyev.solr.birthday.api.BirthdayQuery;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pvasilyev
 * @since 6/9/18
 */
public class BirthdaySearchComponent {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int DAYS_IN_YEAR = 366;

    private String dobField = "client_date_of_birth";
    private TimeZone timeZone;

    /**
     * Main API method which will be responsible for the whole logic of birthday-search.
     *
     * @param query is the query represented as object. Example: search for all people who have birthday within
     *                      <code>birthdayQuery</code> days from now.
     */
    public String doBirthdaySearch(BirthdayQuery query) {
        final int birthdayQuery = query.getDaysToBirthday();
        final int currentDay = getStartDay(query);
        final int currentYear = getCurrentYear(query);
        
        final String currentDayAsString = String.valueOf(currentDay);
        final int endDay;
        final boolean fakeLeapDayWillBeInThResult;
        if (birthdayQuery >= 0) {
            if (birthdayQuery >= DAYS_IN_YEAR) {
                throw new IllegalArgumentException("Can't handle more than days in the year, " +
                        "current query has '" + birthdayQuery + "' days to birthday");
            }
            if (fakeLeapDayWithinNonLeapYear(currentDay, currentYear, birthdayQuery)
                    || fakeLeapDayAsResultOfOverlappingWithNextYear(currentDay, currentYear, birthdayQuery)) {
                endDay = currentDay + birthdayQuery + 1;
                fakeLeapDayWillBeInThResult = true;
            } else {
                fakeLeapDayWillBeInThResult = false;
                endDay = currentDay + birthdayQuery;
            }
        } else {
            throw new IllegalArgumentException("Can't do birthday search backwards, " +
                    "current query has '" + birthdayQuery + "' days to birthday");
        }
        
        final String solrFunctionQuery = daysToBirthdayFunctionScore(currentDayAsString, endDay, fakeLeapDayWillBeInThResult);
        LOG.info("Produced following function-query {}", solrFunctionQuery);

        return solrFunctionQuery;
    }

    private boolean fakeLeapDayWithinNonLeapYear(int currentDay, int currentYear, int birthdayQuery) {
        return !BirthdayQuery.isLeapYear(currentYear)
                && currentDay <= BirthdayQuery.LEAP_DAY_ORD
                && currentDay + birthdayQuery >= BirthdayQuery.LEAP_DAY_ORD;
    }

    private boolean fakeLeapDayAsResultOfOverlappingWithNextYear(int currentDay, int currentYear, int birthdayQuery) {
        return !BirthdayQuery.isLeapYear(currentYear + 1)
                && currentDay + birthdayQuery > DAYS_IN_YEAR
                && (currentDay + birthdayQuery) % DAYS_IN_YEAR + 1 >= BirthdayQuery.LEAP_DAY_ORD;
    }

    private String daysToBirthdayFunctionScore(String currentDayAsString, int endDay, boolean fakeLeapDayWillBeInThResult) {
        return 
                "mod(" +
                        "sub(" +
                            applyShiftDependingOnLeapDayOrd(getDobQueryField(), DAYS_IN_YEAR, endDay, fakeLeapDayWillBeInThResult) +
                            "," +
                            currentDayAsString +
                        ")," +
                        DAYS_IN_YEAR +
                ")";
    }

    private String applyShiftDependingOnLeapDayOrd(String dobField, int additionalShift, int endDay, boolean fakeLeapDayWillBeInThResult) {
        return 
                "sum(" +
                    "if(" +
                        needToApplyLeftShiftForTheDayOrdinal(dobField, endDay, fakeLeapDayWillBeInThResult) + "," +
                        (additionalShift - 1) + "," + // if-clause returned true -> make left shift
                        additionalShift + // otherwise no shift is needed
                    ")," +
                    dobField +
                ")";        
    }

    private String needToApplyLeftShiftForTheDayOrdinal(String dobField, int endDay, boolean fakeLeapDayWillBeInThResult) {
        return 
                "and(" + 
                    fakeLeapDayWillBeInThResult + "," + // must be fake-leap-day within result
                    "and(" + // and day must be after #LEAP_DAY_ORD and before #endDay
                        "not(" + ifLessThan(dobField, mod(endDay, DAYS_IN_YEAR)) + ")," +
                        ifLessThan(dobField, String.valueOf(BirthdayQuery.LEAP_DAY_ORD)) +
                    ")" +
                ")";        
    }

    private String ifLessThan(String leftArg, String rightArg) {
        return "max(0,sub(" + leftArg + "," + rightArg + "))";
    }

    private String mod(int num, int mod) {
        return "add(1,mod(" + num + "," + mod + "))";
    }

    public String createQuery(BirthdayQuery query) {
        final int currentYear = getCurrentYear(query);
        final int startDay = getStartDay(query);
        final String startDayAsString = String.valueOf(startDay);
        final int endDay = getEndDay(query, startDay);
        if (endDay <= DAYS_IN_YEAR) {
            final String endDayAsString;
            if (endDay < BirthdayQuery.LEAP_DAY_ORD || BirthdayQuery.isLeapYear(currentYear)) {
                endDayAsString = String.valueOf(endDay);
            } else {
                endDayAsString = String.valueOf(endDay + 1);
            }
            return getDobQueryField() + ":[" + startDayAsString + " TO " + endDayAsString + "]";
        } else {
            final int overlappedEndDay = endDay - DAYS_IN_YEAR;
            final String endDayAsString;
            if (overlappedEndDay < BirthdayQuery.LEAP_DAY_ORD || BirthdayQuery.isLeapYear(currentYear + 1)) {
                endDayAsString = String.valueOf(overlappedEndDay);
            } else {
                endDayAsString = String.valueOf(overlappedEndDay + 1);
            }
            return getDobQueryField() + ":[" + startDayAsString + " TO " + DAYS_IN_YEAR + "] " +
                    getDobQueryField() + ":[1 TO " + endDayAsString + "]";
        }
    }

    private int getEndDay(BirthdayQuery query, int startDay) {
        final int daysToBirthday = query.getDaysToBirthday();
        return startDay + daysToBirthday;
    }

    private int getStartDay(BirthdayQuery query) {
        return query.getCurrentDayOfYear();
    }

    private int getCurrentYear(BirthdayQuery query) {
        return query.getCurrentYear();
    }

    public String getDobQueryField() {
        return dobField + ".yday";
    }

    public void setDobField(String dobField) {
        this.dobField = dobField;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public SolrInputDocument createSyntheticDobFields(SolrInputDocument document) {
        final SolrInputField dobDocumentField = document.get(dobField);
        if (dobDocumentField == null) {
            throw new IllegalStateException("Can't index document w/o " + dobField + " - birthday search wouldn't have any sense");
        }
        if (dobDocumentField.getValue() == null || !(dobDocumentField.getValue() instanceof Date)) {
            throw new IllegalArgumentException("Can't index document: " + dobField + " is either missing or it is not type of Date");
        }
        final Date dobValue = (Date) dobDocumentField.getValue();
        final Calendar instance = Calendar.getInstance(timeZone);
        instance.setTime(dobValue);
        final int currentYear = instance.get(Calendar.YEAR);
        int dobDayOfYear = instance.get(Calendar.DAY_OF_YEAR);
        if (!BirthdayQuery.isLeapYear(currentYear) && dobDayOfYear >= BirthdayQuery.LEAP_DAY_ORD) {
            dobDayOfYear += 1;
        }
        document.addField(getDobQueryField(), dobDayOfYear);
        document.addField(dobField + ".year", currentYear);

        return document;
    }
}
