package com.github.pvasilyev.solr.birthday;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pvasilyev
 * @since 6/9/18
 */
public class BirthdaySearchComponent {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int LEAP_DAY_ORD = 31 + 29;
    private static final int DAYS_IN_YEAR = 366;

    private static final String DOB_FIELD = "client_date_of_birth.yday";

    /**
     * Main API method which will be responsible for the whole logic of birthday-search.
     *
     * @param birthdayQuery is the query represented as number. Example: search for all people who have birthday within
     *                      <code>birthdayQuery</code> days from now.
     */
    public void doBirthdaySearch(int birthdayQuery) {
        final int currentDay = 1;
        final int currentYear = 2018;
        
        final String currentDayAsString = String.valueOf(currentDay);
        final int endDay;
        final boolean fakeLeapDayWillBeInThResult;
        if (birthdayQuery >= 0) {
            if (fakeLeapDayWithinNonLeapYear(currentDay, currentYear, birthdayQuery)
                    || fakeLeapDayAsResultOfOverlappingWithNextYear(currentDay, currentYear, birthdayQuery)) {
                endDay = currentDay + birthdayQuery + 1;
                fakeLeapDayWillBeInThResult = true;
            } else {
                fakeLeapDayWillBeInThResult = false;
                endDay = currentDay + birthdayQuery;
            }
        } else {
            fakeLeapDayWillBeInThResult = false;
            endDay = currentDay + birthdayQuery;
        }
        
        final String solrFunctionQuery = daysToBirthdayFunctionScore(currentDayAsString, endDay, fakeLeapDayWillBeInThResult);
        LOG.info("Produced following function-query {}", solrFunctionQuery);
    }

    private boolean fakeLeapDayWithinNonLeapYear(int currentDay, int currentYear, int birthdayQuery) {
        return !isLeapYear(currentYear)
                && currentDay <= LEAP_DAY_ORD
                && currentDay + birthdayQuery >= LEAP_DAY_ORD;
    }

    private static boolean isLeapYear(int year) {
        // according to wikipedia:
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    private boolean fakeLeapDayAsResultOfOverlappingWithNextYear(int currentDay, int currentYear, int birthdayQuery) {
        return !isLeapYear(currentYear + 1)
                && currentDay + birthdayQuery > DAYS_IN_YEAR
                && (currentDay + birthdayQuery) % DAYS_IN_YEAR + 1 >= LEAP_DAY_ORD;
    }

    private String daysToBirthdayFunctionScore(String currentDayAsString, int endDay, boolean fakeLeapDayWillBeInThResult) {
        return 
                "mod(" +
                        "sub(" +
                            applyShiftDependingOnLeapDayOrd(DOB_FIELD, DAYS_IN_YEAR, endDay, fakeLeapDayWillBeInThResult) +
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
                        ifLessThan(dobField, String.valueOf(LEAP_DAY_ORD)) +
                    ")" +
                ")";        
    }

    private String ifLessThan(String leftArg, String rightArg) {
        return "max(0,sub(" + leftArg + "," + rightArg + "))";
    }

    private String mod(int endDay, int mod) {
        return "add(1,mod(" + endDay + "," + mod + "))"; 
    }

}
