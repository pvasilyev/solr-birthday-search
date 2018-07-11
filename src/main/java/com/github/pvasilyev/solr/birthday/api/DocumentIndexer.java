package com.github.pvasilyev.solr.birthday.api;

import org.apache.solr.common.SolrInputDocument;

/**
 * This is main abstraction over indexer logic.
 */
public interface DocumentIndexer {

    /**
     * This method aims to index document as well as enrich it with any required meta-data.
     *
     * @param document is the document we are going to index in search engine.
     */
    void index(SolrInputDocument document);

    /**
     * This method commits all pending inxing operations.
     */
    void commit();

    /**
     * This method aims to clear-up all storage, aka revoke all documents (even those which were committed).
     */
    void deleteAll();

}
