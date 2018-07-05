package com.github.pvasilyev.solr.birthday;

public class FullBirthdaySearchNowIsNonLeapYearTest extends BaseBirthdaySearchFullTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        currentYear = "2017";
    }
}
