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

public class FullBirthdaySearchNowIsNonLeapYearTest extends BaseBirthdaySearchFullTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        currentYear = "2017";
    }

    @Test
    public void birthdaySearchBefore1stMar() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate("2015-02-27 15:00:00")) // Feb 27 current year is non leap
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
    public void birthdaySearchAfter1stMar() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate("2019-03-02 15:00:00")) // Mar 2, current year is non leap
                .withDaysToBirthday(1)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(2));

        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("36025"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Kent Clark"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(62));
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1966));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(0.0D, 1E-6));

        Assert.assertThat(result.get(1), IsNull.notNullValue());
        Assert.assertThat(result.get(1).get("id"), IsEqual.equalTo("77075"));
        Assert.assertThat(result.get(1).get("client_name_s"), IsEqual.equalTo("Mickey Mouse"));
        Assert.assertThat(result.get(1).get("client_date_of_birth.yday"), IsEqual.equalTo(63));
        Assert.assertThat(result.get(1).get("client_date_of_birth.year"), IsEqual.equalTo(1965));
        Assert.assertThat(Double.valueOf((Float)result.get(1).get("days_to_birthday")), IsCloseTo.closeTo(1.0D, 1E-6));
    }

    @Test
    public void birthdaySearchAround1stMar() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate( "2014-02-27 15:00:00")) // Feb 27, current year is non leap
                .withDaysToBirthday(5)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(6));

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

        Assert.assertThat(result.get(2), IsNull.notNullValue());
        Assert.assertThat(result.get(2).get("id"), IsEqual.equalTo("77739"));
        Assert.assertThat(result.get(2).get("client_name_s"), IsEqual.equalTo("Robin Hood"));
        Assert.assertThat(result.get(2).get("client_date_of_birth.yday"), IsEqual.equalTo(60));
        Assert.assertThat(result.get(2).get("client_date_of_birth.year"), IsEqual.equalTo(1952));
        Assert.assertThat(Double.valueOf((Float)result.get(2).get("days_to_birthday")), IsCloseTo.closeTo(2.0D, 1E-6));

        Assert.assertThat(result.get(3), IsNull.notNullValue());
        Assert.assertThat(result.get(3).get("id"), IsEqual.equalTo("76642"));
        Assert.assertThat(result.get(3).get("client_name_s"), IsEqual.equalTo("Winnie the Pooh"));
        Assert.assertThat(result.get(3).get("client_date_of_birth.yday"), IsEqual.equalTo(61));
        Assert.assertThat(result.get(3).get("client_date_of_birth.year"), IsEqual.equalTo(1938));
        Assert.assertThat(Double.valueOf((Float)result.get(3).get("days_to_birthday")), IsCloseTo.closeTo(2.0D, 1E-6));

        Assert.assertThat(result.get(4), IsNull.notNullValue());
        Assert.assertThat(result.get(4).get("id"), IsEqual.equalTo("36025"));
        Assert.assertThat(result.get(4).get("client_name_s"), IsEqual.equalTo("Kent Clark"));
        Assert.assertThat(result.get(4).get("client_date_of_birth.yday"), IsEqual.equalTo(62));
        Assert.assertThat(result.get(4).get("client_date_of_birth.year"), IsEqual.equalTo(1966));
        Assert.assertThat(Double.valueOf((Float)result.get(4).get("days_to_birthday")), IsCloseTo.closeTo(3.0D, 1E-6));

        Assert.assertThat(result.get(5), IsNull.notNullValue());
        Assert.assertThat(result.get(5).get("id"), IsEqual.equalTo("77075"));
        Assert.assertThat(result.get(5).get("client_name_s"), IsEqual.equalTo("Mickey Mouse"));
        Assert.assertThat(result.get(5).get("client_date_of_birth.yday"), IsEqual.equalTo(63));
        Assert.assertThat(result.get(5).get("client_date_of_birth.year"), IsEqual.equalTo(1965));
        Assert.assertThat(Double.valueOf((Float)result.get(5).get("days_to_birthday")), IsCloseTo.closeTo(4.0D, 1E-6));
    }
}
