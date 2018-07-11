package com.github.pvasilyev.solr.birthday.impl;

import com.github.pvasilyev.solr.birthday.api.BirthdayQuery;
import com.github.pvasilyev.solr.birthday.api.SearchProvider;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of the {@link SearchProvider}.
 *
 * <p>It is designed to be useful for integration with Spring. But it's up to end user how to configure this component.
 *
 */
public class SearchProviderImpl implements SearchProvider {

    private SearchEngineFactory searchEngineFactory;
    private BirthdaySearchComponent birthdaySearchComponent;
    private String collectionName = "test";

    @Override
    public List<SolrDocument> searchByBirthday(BirthdayQuery query) {
        final ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.add(CommonParams.Q, birthdaySearchComponent.createQuery(query));
        final String functionQuery = birthdaySearchComponent.doBirthdaySearch(query);
        solrParams.add(CommonParams.FL, "*,days_to_birthday:" + functionQuery);
        solrParams.add(CommonParams.SORT, functionQuery + " " +
                SolrQuery.ORDER.asc + ", " + birthdaySearchComponent.getDobQueryField() + " " + SolrQuery.ORDER.asc);
        solrParams.add(CommonParams.ROWS, String.valueOf(query.getRows()));

        try {
            final SolrClient solrClient = searchEngineFactory.getSolrClientInstance();
            final QueryResponse queryResponse = solrClient.query(collectionName, solrParams, SolrRequest.METHOD.POST);
            final SolrDocumentList results = queryResponse.getResults();

            return new ArrayList<>(results);
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException("Issue when calling Search engine", e);
        }
    }

    public void setSearchEngineFactory(SearchEngineFactory searchEngineFactory) {
        this.searchEngineFactory = searchEngineFactory;
    }

    public void setBirthdaySearchComponent(BirthdaySearchComponent birthdaySearchComponent) {
        this.birthdaySearchComponent = birthdaySearchComponent;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

}
