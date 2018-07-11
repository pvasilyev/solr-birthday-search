package com.github.pvasilyev.solr.birthday.impl;

import com.github.pvasilyev.solr.birthday.api.DocumentIndexer;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

public class DocumentIndexerImpl implements DocumentIndexer {

    private SearchEngineFactory searchEngineFactory;
    private BirthdaySearchComponent birthdaySearchComponent;
    private String collectionName = "test";

    @Override
    public void index(SolrInputDocument enrichedDocument) {
        final SolrClient solrClient = searchEngineFactory.getSolrClientInstance();
        enrichedDocument = birthdaySearchComponent.createSyntheticDobFields(enrichedDocument);
        try {
            solrClient.add(collectionName, enrichedDocument);
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException("Can't index document due to internal Solr error", e);
        }
    }

    @Override
    public void commit() {
        final SolrClient solrClient = searchEngineFactory.getSolrClientInstance();
        try {
            solrClient.commit(collectionName);
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException("Can't commit due to internal Solr error", e);
        }
    }

    @Override
    public void deleteAll() {
        final SolrClient solrClient = searchEngineFactory.getSolrClientInstance();
        try {
            solrClient.deleteByQuery(collectionName, "*:*");
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException("Can't delete-all due to internal Solr error", e);
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
