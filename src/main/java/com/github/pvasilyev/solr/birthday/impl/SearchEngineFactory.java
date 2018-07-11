package com.github.pvasilyev.solr.birthday.impl;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

public class SearchEngineFactory {

    private SolrClient solrClient;

    /**
     * Implement this method in order to configure Production-ready connection to the Solr.
     */
    @PostConstruct
    public void postConstruct() {
        final String solrUrl = "http://localhost:8983/solr/";
        solrClient = new HttpSolrClient.Builder(solrUrl).build();
    }

    public SolrClient getSolrClientInstance() {
        return solrClient;
    }

    @PreDestroy
    public void preDestroy() {
        IOUtils.closeQuietly(solrClient);
    }

}
