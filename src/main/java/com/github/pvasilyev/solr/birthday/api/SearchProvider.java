package com.github.pvasilyev.solr.birthday.api;

import org.apache.solr.common.SolrDocument;

import java.util.List;

/**
 * This is main abstraction over searcher logic.
 */
public interface SearchProvider {

    List<SolrDocument> searchByBirthday(BirthdayQuery query);

}
