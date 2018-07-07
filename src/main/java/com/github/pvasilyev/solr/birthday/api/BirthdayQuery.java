package com.github.pvasilyev.solr.birthday.api;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Query object which holds all information and context needed for making search by birthday.
 */
public class BirthdayQuery {

    public static final int LEAP_DAY_ORD = 31 + 29;

    private final int currentYear;
    private final int currentDayOfYear;
    private final int daysToBirthday;
    private final int rows;

    private BirthdayQuery(int currentYear, int currentDayOfYear, int daysToBirthday, int rows) {
        this.currentYear = currentYear;
        this.currentDayOfYear = currentDayOfYear;
        this.daysToBirthday = daysToBirthday;
        this.rows = rows;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public int getCurrentDayOfYear() {
        return currentDayOfYear;
    }

    public int getDaysToBirthday() {
        return daysToBirthday;
    }

    public int getRows() {
        return rows;
    }

    /**
     * Implemented according to the wikipedia, see <a href="https://en.wikipedia.org/wiki/Leap_year">Leap year</a>.
     *
     * @param year is the year to check whether leap or not.
     * @return true iff the <code>year</code> is leap, false otherwise.
     */
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    /**
     * Helper class which aims to construct the object of {@link BirthdayQuery}.
     * Use it in order to chain your construction of the birthday query.
     */
    public static class Builder {

        private Date currentTime = new Date();
        private int daysToBirthday;
        private TimeZone timeZone = TimeZone.getDefault();
        private int rows = 10;

        public Builder withCurrentTime(Date currentTime) {
            this.currentTime = currentTime;
            return this;
        }

        public Builder withDaysToBirthday(int daysToBirthday) {
            this.daysToBirthday = daysToBirthday;
            return this;
        }

        public Builder withTimeZone(TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder withRows(int rows) {
            this.rows = rows;
            return this;
        }

        public BirthdayQuery build() {
            final Calendar instance = Calendar.getInstance(timeZone);
            instance.setTime(currentTime);
            final int currentYear = instance.get(Calendar.YEAR);
            int currentDayOfYear = instance.get(Calendar.DAY_OF_YEAR);
            if (!isLeapYear(currentYear) && currentDayOfYear >= LEAP_DAY_ORD) {
                currentDayOfYear += 1;
            }
            return new BirthdayQuery(currentYear, currentDayOfYear, daysToBirthday, rows);
        }

    }

}
