package com.github.pvasilyev.solr.birthday.api;

import java.util.TimeZone;

/**
 * Query object which holds all information and context needed for making search by birthday.
 */
public class BirthdayQuery {

    private final int daysToBirthday;
    private final TimeZone timeZone;
    private final int rows;

    private BirthdayQuery(int daysToBirthday, TimeZone timeZone, int rows) {
        this.daysToBirthday = daysToBirthday;
        this.timeZone = timeZone;
        this.rows = rows;
    }

    public int getDaysToBirthday() {
        return daysToBirthday;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public int getRows() {
        return rows;
    }

    public static class Builder {

        private int daysToBirthday;
        private TimeZone timeZone;
        private int rows = 5;

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
            return new BirthdayQuery(daysToBirthday, timeZone, rows);
        }

    }

}
