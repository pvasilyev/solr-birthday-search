package com.github.pvasilyev.solr.birthday.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.io.IOException;

public class SearchEngineFactory {

    public SolrClient createSolrClient() {
        final String solrUrl = "http://localhost:8983/solr/";
        return new HttpSolrClient.Builder(solrUrl).build();
    }

}
