package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.api.DocumentIndexer;
import com.github.pvasilyev.solr.birthday.api.SearchProvider;
import com.github.pvasilyev.solr.birthday.impl.BirthdaySearchComponent;
import com.github.pvasilyev.solr.birthday.impl.DocumentIndexerImpl;
import com.github.pvasilyev.solr.birthday.impl.SearchEngineFactory;
import com.github.pvasilyev.solr.birthday.impl.SearchProviderImpl;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class AbstractBirthdaySearchTest {

    static final String COLLECTION = "sample";
    static final String DATE_OF_BIRTH_FIELD = "client_date_of_birth";

    SearchProvider provider;
    SearchEngineFactory searchEngineFactory;
    DocumentIndexer indexer;

    private TimeZone gmt = TimeZone.getTimeZone("GMT");


    @Before
    public void setUp() throws Exception {
        final SearchProviderImpl internalProvider = new SearchProviderImpl();
        final BirthdaySearchComponent birthdaySearchComponent = new BirthdaySearchComponent();
        birthdaySearchComponent.setDobField(DATE_OF_BIRTH_FIELD);
        birthdaySearchComponent.setTimeZone(gmt);
        internalProvider.setBirthdaySearchComponent(birthdaySearchComponent);
        searchEngineFactory = new TestSearchEngineFactory();
        searchEngineFactory.postConstruct();
        internalProvider.setSearchEngineFactory(searchEngineFactory);
        internalProvider.setCollectionName(COLLECTION);
        provider = internalProvider;

        final DocumentIndexerImpl internalIndexer = new DocumentIndexerImpl();
        internalIndexer.setBirthdaySearchComponent(birthdaySearchComponent);
        internalIndexer.setCollectionName(COLLECTION);
        internalIndexer.setSearchEngineFactory(searchEngineFactory);
        indexer = internalIndexer;

        fillInWithSomeData();
    }

    protected abstract void fillInWithSomeData() throws Exception;

    SolrInputDocument createDoc(String id, String name, String year, String month, String day) {
        final SolrInputDocument inputDocument = new SolrInputDocument("id", id, "client_name_s", name);
        final Date dobDate = fromStringDate(year + "-" + month + "-" + day + " 15:00:00");
        inputDocument.addField(DATE_OF_BIRTH_FIELD, dobDate);
        return inputDocument;
    }

    Date fromStringDate(String dateAsString) {
        try {
            return fromStringDate(dateAsString, TimeZone.getTimeZone("GMT"));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Can't parse the date " + dateAsString);
        }
    }

    private Date fromStringDate(String dateAsString, TimeZone timeZone) throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(timeZone);
        return dateFormat.parse(dateAsString);
    }

}
