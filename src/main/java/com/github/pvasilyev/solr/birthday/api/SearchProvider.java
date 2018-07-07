package com.github.pvasilyev.solr.birthday.api;

import org.apache.solr.common.SolrDocument;

import java.util.List;

/**
 * This is main abstraction over searcher logic.
 */
public interface SearchProvider {

    /**
     * Main method which aims to find Solr documents by given search criteria which is encapsulated in
     * <code>query</code>.
     *
     * @param query contains entire context of the birthday search request.
     * @return the list of Solr documents which satisfy the given criteria <code>query</code>.
     *          Documents in the list are sorted by the natural order. The document will be placed in the beginning
     *          of the list as long as its birthday closer to current date given by the criteria <code>query</code>.
     */
    List<SolrDocument> searchByBirthday(BirthdayQuery query);

}
