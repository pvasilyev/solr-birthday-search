package com.github.pvasilyev.solr.birthday;

public class FullBirthdaySearchNowIsLeapYearTest extends BaseBirthdaySearchFullTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        currentYear = "2016";
    }
}
