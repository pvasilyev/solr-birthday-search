package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.api.BirthdayQuery;
import com.github.pvasilyev.solr.birthday.api.SearchProvider;
import com.github.pvasilyev.solr.birthday.impl.BirthdaySearchComponent;
import com.github.pvasilyev.solr.birthday.impl.SearchProviderImpl;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.hamcrest.number.IsCloseTo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class BirthdaySearchComponentTest {

    private static final String COLLECTION = "sample";
    private SearchProvider provider;
    private SearchProviderImpl internalProvider;

    @Before
    public void setUp() throws Exception {
        internalProvider = new SearchProviderImpl();
        final BirthdaySearchComponent birthdaySearchComponent = new BirthdaySearchComponent();
        internalProvider.setBirthdaySearchComponent(birthdaySearchComponent);
        internalProvider.setSolrClient(new TestSearchEngineFactory());
        internalProvider.setCollectionName(COLLECTION);
        this.provider = internalProvider;

        fillInWithSomeData();
    }

    private void fillInWithSomeData() throws IOException, SolrServerException {
        final SolrClient solrClient = internalProvider.getSolrClient();
        solrClient.add(COLLECTION, createDoc("4352", "Adam Peters", "40", "1984"));
        solrClient.add(COLLECTION, createDoc("5412", "Bob Dylan", "58", "1990"));
        solrClient.add(COLLECTION, createDoc("2983", "Charlie Shin", "73", "1973"));
        final UpdateResponse commit = solrClient.commit(COLLECTION);
        Assert.assertThat(commit, IsNull.notNullValue());
    }

    private SolrInputDocument createDoc(String id, String name, String yday, String year) {
        return new SolrInputDocument("id", id, "client_name_s", name,
                "client_date_of_birth.yday", yday, "client_date_of_birth.year", year);
    }

    @Test
    public void embeddedSolrWorks() throws Exception {
        final TestSearchEngineFactory testSearchEngineFactory = new TestSearchEngineFactory();
        final SolrClient solrClient = testSearchEngineFactory.createSolrClient();

        solrClient.add(COLLECTION, new SolrInputDocument("id", "123", "client_first_name_s", "John"));
        solrClient.add(COLLECTION, new SolrInputDocument("id", "987", "client_first_name_s", "Peter"));
        final UpdateResponse updateResponse = solrClient.commit(COLLECTION);
        Assert.assertThat(updateResponse, IsNull.notNullValue());

        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.add(CommonParams.Q, "*:*");
        final QueryResponse response = solrClient.query(COLLECTION, solrParams);
        Assert.assertThat(response, IsNull.notNullValue());
        Assert.assertThat(response.getResults().getNumFound(), IsEqual.equalTo(2L));

        solrClient.close();
    }

    @Test
    public void simpleBirthdaySearchFrom10thMar() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate("2018-03-10 15:00:00"))
                .withDaysToBirthday(10)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .withRows(1)
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(1));
        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("2983"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Charlie Shin"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(73)); // 73-th day is 13th Mar (31+29+13)
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1973));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(3.0D, 1E-6));
    }

    @Test
    public void simpleBirthdaySearchFrom5thFeb() throws Exception {
        final BirthdayQuery birthdayQuery = new BirthdayQuery.Builder()
                .withCurrentTime(fromStringDate("2018-02-05 15:00:00"))
                .withDaysToBirthday(10)
                .withTimeZone(TimeZone.getTimeZone("GMT"))
                .withRows(1)
                .build();
        final List<SolrDocument> result = provider.searchByBirthday(birthdayQuery);
        Assert.assertThat(result, IsNull.notNullValue());
        Assert.assertThat(result.size(), IsEqual.equalTo(1));
        Assert.assertThat(result.get(0), IsNull.notNullValue());
        Assert.assertThat(result.get(0).get("id"), IsEqual.equalTo("4352"));
        Assert.assertThat(result.get(0).get("client_name_s"), IsEqual.equalTo("Adam Peters"));
        Assert.assertThat(result.get(0).get("client_date_of_birth.yday"), IsEqual.equalTo(40)); // 40-th day is 9th Feb (31+9)
        Assert.assertThat(result.get(0).get("client_date_of_birth.year"), IsEqual.equalTo(1984));
        Assert.assertThat(Double.valueOf((Float)result.get(0).get("days_to_birthday")), IsCloseTo.closeTo(4.0D, 1E-6));
    }

    private Date fromStringDate(String dateAsString) throws ParseException {
        return fromStringDate(dateAsString, TimeZone.getTimeZone("GMT"));
    }

    private Date fromStringDate(String dateAsString, TimeZone timeZone) throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(timeZone);
        return dateFormat.parse(dateAsString);
    }

    @After
    public void tearDown() throws Exception {
        internalProvider.close();
    }
}
