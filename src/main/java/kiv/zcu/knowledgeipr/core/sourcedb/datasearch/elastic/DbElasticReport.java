package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.DbReport;

import java.util.ArrayList;
import java.util.List;

// TODO: Create and implement DbMongoReport the same way
public class DbElasticReport extends DbReport<ElasticRecord> {

    public DbElasticReport(List<ElasticRecord> records, long docsCount) {
        super(records, docsCount);
    }

    DbElasticReport() {
        this(new ArrayList<>(), 0);
    }
}
