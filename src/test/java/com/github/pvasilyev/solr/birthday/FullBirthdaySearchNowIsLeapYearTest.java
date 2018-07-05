package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.api.BirthdayQuery;
import org.apache.solr.common.SolrDocument;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.hamcrest.number.IsCloseTo;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.TimeZone;

public class FullBirthdaySearchNowIsLeapYearTest extends BaseBirthdaySearchFullTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        currentYear = "2016";
    }

    @Test
    public void birthdaySearchBefore29thFeb() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate("2004-02-27 15:00:00")) // Feb 27
                .withDaysToBirthday(1)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(2));

        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("13847"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Peter Parker"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(58));
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1981));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(0.0D, 1E-6));

        Assert.assertThat(result.get(1), IsNull.notNullValue());
        Assert.assertThat(result.get(1).get("id"), IsEqual.equalTo("76440"));
        Assert.assertThat(result.get(1).get("client_name_s"), IsEqual.equalTo("Bruce Wayne"));
        Assert.assertThat(result.get(1).get("client_date_of_birth.yday"), IsEqual.equalTo(59));
        Assert.assertThat(result.get(1).get("client_date_of_birth.year"), IsEqual.equalTo(1966));
        Assert.assertThat(Double.valueOf((Float)result.get(1).get("days_to_birthday")), IsCloseTo.closeTo(1.0D, 1E-6));
    }

    @Test
    public void birthdaySearchAfter29thFeb() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate("2008-02-28 15:00:00")) // Feb 28, current year is leap
                .withDaysToBirthday(1)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(2));

        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("76440"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Bruce Wayne"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(59));
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1966));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(0.0D, 1E-6));

        Assert.assertThat(result.get(1), IsNull.notNullValue());
        Assert.assertThat(result.get(1).get("id"), IsEqual.equalTo("77739"));
        Assert.assertThat(result.get(1).get("client_name_s"), IsEqual.equalTo("Robin Hood"));
        Assert.assertThat(result.get(1).get("client_date_of_birth.yday"), IsEqual.equalTo(60));
        Assert.assertThat(result.get(1).get("client_date_of_birth.year"), IsEqual.equalTo(1952));
        Assert.assertThat(Double.valueOf((Float)result.get(1).get("days_to_birthday")), IsCloseTo.closeTo(1.0D, 1E-6));
    }

    @Test
    public void birthdaySearchRightOn29thFeb() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate("2012-02-28 15:00:00")) // Feb 28, current year is leap
                .withDaysToBirthday(0)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(1));

        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("76440"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Bruce Wayne"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(59));
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1966));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(0.0D, 1E-6));
    }
}
