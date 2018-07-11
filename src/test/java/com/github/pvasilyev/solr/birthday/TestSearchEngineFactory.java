package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.impl.SearchEngineFactory;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URL;

public class TestSearchEngineFactory extends SearchEngineFactory {

    private EmbeddedSolrServer embeddedSolrServer;

    @PostConstruct
    @Override
    public void postConstruct() {
        final URL resource = this.getClass().getResource("/solr");
        CoreContainer container = new CoreContainer(resource.getFile());
        container.load();
        embeddedSolrServer = new EmbeddedSolrServer(container, "sample");
    }

    @Override
    public SolrClient getSolrClientInstance() {
        return embeddedSolrServer;
    }

    @PreDestroy
    @Override
    public void preDestroy() {
        IOUtils.closeQuietly(embeddedSolrServer);
    }
}
