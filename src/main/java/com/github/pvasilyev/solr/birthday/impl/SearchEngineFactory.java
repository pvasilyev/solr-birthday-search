package com.github.pvasilyev.solr.birthday.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;

public class SearchEngineFactory {

    /**
     * Implement this method in order to configure Production-ready connection to the Solr.
     *
     * @return Solr-client (either Http or Cloud) which will talk to the real Solr cluster.
     */
    public SolrClient createSolrClient() {
        final String solrUrl = "http://localhost:8983/solr/";
        return new HttpSolrClient.Builder(solrUrl).build();
    }

}
