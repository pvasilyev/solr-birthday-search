package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.api.SearchProvider;
import com.github.pvasilyev.solr.birthday.impl.BirthdaySearchComponent;
import com.github.pvasilyev.solr.birthday.impl.SearchProviderImpl;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BirthdaySearchComponentTest {

    private static final String COLLECTION = "sample";
    private SearchProvider provider;
    private SearchProviderImpl internalProvider;

    @Before
    public void setUp() {
        internalProvider = new SearchProviderImpl();
        final BirthdaySearchComponent birthdaySearchComponent = new BirthdaySearchComponent();
        internalProvider.setBirthdaySearchComponent(birthdaySearchComponent);
        internalProvider.setSolrClient(new TestSearchEngineFactory());
        internalProvider.setCollectionName(COLLECTION);
        this.provider = internalProvider;
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

    @After
    public void tearDown() throws Exception {
        internalProvider.close();
    }
}
