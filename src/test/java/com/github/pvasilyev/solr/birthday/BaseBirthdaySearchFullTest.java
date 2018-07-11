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

public abstract class BaseBirthdaySearchFullTest extends AbstractBirthdaySearchTest {

    String currentYear;

    @Override
    protected void fillInWithSomeData() {
        indexer.deleteAll();

        // leap year use-cases:
        indexDoB63();
        indexDoB62();
        indexDoB61();
        indexDoB60();
        indexDoB59();
        indexDoB58();

        // Jan 1st use-cases:
        indexDoB366();
        indexDoB365();
        indexDoB1();
        indexDoB2();

        indexer.commit();
    }

    private void indexDoB2() {
        indexer.index(createDoc("43405", "Darkwing Duck", "1953", "01", "2"));
    }

    private void indexDoB1() {
        indexer.index(createDoc("27523", "Dippy Dawg", "1947", "01", "1"));
    }

    private void indexDoB365() {
        indexer.index(createDoc("89794", "Steven Rogers", "1967", "12", "30"));
    }

    private void indexDoB366() {
        indexer.index(createDoc("31516", "Donald Duck", "1958", "12", "31"));
    }

    private void indexDoB58() {
        indexer.index(createDoc("13847", "Peter Parker", "1981", "02", "27"));
    }

    private void indexDoB59() {
        indexer.index(createDoc("76440", "Bruce Wayne", "1966", "02", "28"));
    }

    private void indexDoB60() {
        indexer.index(createDoc("77739", "Robin Hood", "1952", "02", "29"));
    }

    private void indexDoB61() {
        indexer.index(createDoc("76642", "Winnie the Pooh", "1938", "03", "01"));
    }

    private void indexDoB62() {
        indexer.index(createDoc("36025", "Kent Clark", "1966", "03", "02"));
    }

    private void indexDoB63() {
        indexer.index(createDoc("77075", "Mickey Mouse", "1965", "03", "03"));
    }

    @Test
    public void birthdaySearchBefore1stJan() {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate(currentYear + "-12-30 15:00:00")) // Dec 30
                .withDaysToBirthday(1)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(2));

        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("89794"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Steven Rogers"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(365));
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1967));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(0.0D, 1E-6));

        Assert.assertThat(result.get(1), IsNull.notNullValue());
        Assert.assertThat(result.get(1).get("id"), IsEqual.equalTo("31516"));
        Assert.assertThat(result.get(1).get("client_name_s"), IsEqual.equalTo("Donald Duck"));
        Assert.assertThat(result.get(1).get("client_date_of_birth.yday"), IsEqual.equalTo(366));
        Assert.assertThat(result.get(1).get("client_date_of_birth.year"), IsEqual.equalTo(1958));
        Assert.assertThat(Double.valueOf((Float)result.get(1).get("days_to_birthday")), IsCloseTo.closeTo(1.0D, 1E-6));
    }

    @Test
    public void birthdaySearchAfter1stJan() {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate(currentYear + "-01-01 15:00:00")) // Jan 1
                .withDaysToBirthday(1)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(2));

        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("27523"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Dippy Dawg"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(1));
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1947));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(0.0D, 1E-6));

        Assert.assertThat(result.get(1), IsNull.notNullValue());
        Assert.assertThat(result.get(1).get("id"), IsEqual.equalTo("43405"));
        Assert.assertThat(result.get(1).get("client_name_s"), IsEqual.equalTo("Darkwing Duck"));
        Assert.assertThat(result.get(1).get("client_date_of_birth.yday"), IsEqual.equalTo(2));
        Assert.assertThat(result.get(1).get("client_date_of_birth.year"), IsEqual.equalTo(1953));
        Assert.assertThat(Double.valueOf((Float)result.get(1).get("days_to_birthday")), IsCloseTo.closeTo(1.0D, 1E-6));
    }

    @Test
    public void birthdaySearchAround1stJan() {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate(currentYear + "-12-30 15:00:00")) // Dec 30
                .withDaysToBirthday(3)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .withRows(5)
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(4));

        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("89794"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Steven Rogers"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(365));
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1967));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(0.0D, 1E-6));

        Assert.assertThat(result.get(1), IsNull.notNullValue());
        Assert.assertThat(result.get(1).get("id"), IsEqual.equalTo("31516"));
        Assert.assertThat(result.get(1).get("client_name_s"), IsEqual.equalTo("Donald Duck"));
        Assert.assertThat(result.get(1).get("client_date_of_birth.yday"), IsEqual.equalTo(366));
        Assert.assertThat(result.get(1).get("client_date_of_birth.year"), IsEqual.equalTo(1958));
        Assert.assertThat(Double.valueOf((Float)result.get(1).get("days_to_birthday")), IsCloseTo.closeTo(1.0D, 1E-6));

        Assert.assertThat(result.get(2), IsNull.notNullValue());
        Assert.assertThat(result.get(2).get("id"), IsEqual.equalTo("27523"));
        Assert.assertThat(result.get(2).get("client_name_s"), IsEqual.equalTo("Dippy Dawg"));
        Assert.assertThat(result.get(2).get("client_date_of_birth.yday"), IsEqual.equalTo(1));
        Assert.assertThat(result.get(2).get("client_date_of_birth.year"), IsEqual.equalTo(1947));
        Assert.assertThat(Double.valueOf((Float)result.get(2).get("days_to_birthday")), IsCloseTo.closeTo(2.0D, 1E-6));

        Assert.assertThat(result.get(3), IsNull.notNullValue());
        Assert.assertThat(result.get(3).get("id"), IsEqual.equalTo("43405"));
        Assert.assertThat(result.get(3).get("client_name_s"), IsEqual.equalTo("Darkwing Duck"));
        Assert.assertThat(result.get(3).get("client_date_of_birth.yday"), IsEqual.equalTo(2));
        Assert.assertThat(result.get(3).get("client_date_of_birth.year"), IsEqual.equalTo(1953));
        Assert.assertThat(Double.valueOf((Float)result.get(3).get("days_to_birthday")), IsCloseTo.closeTo(3.0D, 1E-6));
    }
}
