package com.github.pvasilyev.solr.birthday;

import com.github.pvasilyev.solr.birthday.api.SearchProvider;
import com.github.pvasilyev.solr.birthday.impl.BirthdaySearchComponent;
import com.github.pvasilyev.solr.birthday.impl.SearchProviderImpl;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public abstract class AbstractBirthdaySearchTest {

    static final String COLLECTION = "sample";
    SearchProvider provider;
    SearchProviderImpl internalProvider;


    @Before
    public void setUp() throws Exception {
        internalProvider = new SearchProviderImpl();
        final BirthdaySearchComponent birthdaySearchComponent = new BirthdaySearchComponent();
        birthdaySearchComponent.setDobField("client_date_of_birth.yday");
        internalProvider.setBirthdaySearchComponent(birthdaySearchComponent);
        internalProvider.setSolrClient(new TestSearchEngineFactory());
        internalProvider.setCollectionName(COLLECTION);
        this.provider = internalProvider;

        fillInWithSomeData();
    }

    protected abstract void fillInWithSomeData() throws Exception;

    SolrInputDocument createDoc(String id, String name, String yday, String year) {
        return new SolrInputDocument("id", id, "client_name_s", name,
                "client_date_of_birth.yday", yday, "client_date_of_birth.year", year);
    }

    Date fromStringDate(String dateAsString) throws ParseException {
        return fromStringDate(dateAsString, TimeZone.getTimeZone("GMT"));
    }

    private Date fromStringDate(String dateAsString, TimeZone timeZone) throws ParseException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(timeZone);
        return dateFormat.parse(dateAsString);
    }

}
