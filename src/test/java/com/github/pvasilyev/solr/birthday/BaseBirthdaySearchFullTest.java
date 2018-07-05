package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.api.BirthdayQuery;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.hamcrest.number.IsCloseTo;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

public abstract class BaseBirthdaySearchFullTest extends AbstractBirthdaySearchTest {

    String currentYear;

    @Override
    protected void fillInWithSomeData() throws Exception {
        final SolrClient solrClient = internalProvider.getSolrClient();
        solrClient.deleteByQuery(COLLECTION, "*:*");

        // leap year use-cases:
        indexDoB63(solrClient);
        indexDoB62(solrClient);
        indexDoB61(solrClient);
        indexDoB60(solrClient);
        indexDoB59(solrClient);
        indexDoB58(solrClient);

        // Jan 1st use-cases:
        indexDoB366(solrClient);
        indexDoB365(solrClient);
        indexDoB1(solrClient);
        indexDoB2(solrClient);

        solrClient.commit(COLLECTION);
    }

    private void indexDoB2(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("43405", "Darkwing Duck", "2", "1953"));
    }

    private void indexDoB1(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("27523", "Dippy Dawg", "1", "1947"));
    }

    private void indexDoB365(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("89794", "Steven Rogers", "365", "1967"));
    }

    private void indexDoB366(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("31516", "Donald Duck", "366", "1958"));
    }

    private void indexDoB58(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("13847", "Peter Parker", "58", "1981"));
    }

    private void indexDoB59(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("76440", "Bruce Wayne", "59", "1966"));
    }

    private void indexDoB60(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("77739", "Robin Hood", "60", "1952"));
    }

    private void indexDoB61(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("76642", "Winnie the Pooh", "61", "1938"));
    }

    private void indexDoB62(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("36025", "Kent Clark", "62", "1966"));
    }

    private void indexDoB63(SolrClient solrClient) throws IOException, SolrServerException {
        solrClient.add(COLLECTION, createDoc("77075", "Mickey Mouse", "63", "1965"));
    }

    @Test
    public void birthdaySearchBefore1stJan() throws Exception {
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
    public void birthdaySearchAfter1stJan() throws Exception {
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
    public void birthdaySearchAround1stJan() throws Exception {
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
