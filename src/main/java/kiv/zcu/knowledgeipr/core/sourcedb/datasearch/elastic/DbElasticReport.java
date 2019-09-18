package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.DbReport;

import java.util.ArrayList;
import java.util.List;

// TODO: Create and implement DbMongoReport the same way

/**
 * Wraps retrieved records from the ElasticSearch with other data, like the total count of documents.
 */
public class DbElasticReport extends DbReport<ElasticRecord> {

    public DbElasticReport(List<ElasticRecord> records, long totalDocs) {
        super(records, totalDocs);
    }

    DbElasticReport() {
        this(new ArrayList<>(), 0);
    }
}
