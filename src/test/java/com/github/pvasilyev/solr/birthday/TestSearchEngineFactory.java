package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.impl.SearchEngineFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

import java.io.IOException;
import java.net.URL;

public class TestSearchEngineFactory extends SearchEngineFactory {

    private EmbeddedSolrServer embeddedSolrServer;

    @Override
    public SolrClient createSolrClient() {
        final URL resource = this.getClass().getResource("/solr");
        CoreContainer container = new CoreContainer(resource.getFile());
        container.load();
        embeddedSolrServer = new EmbeddedSolrServer(container, "sample");
        return embeddedSolrServer;
    }

}
