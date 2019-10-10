package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.DbReport;

import java.util.ArrayList;
import java.util.List;

// TODO: Create and implement DbMongoReport the same way

/**
 * Wraps retrieved records from the ElasticSearch with other data, like the total count of documents.
 */
public class DbElasticReportWrapper extends DbReport<ElasticRecord> {

    /**
     * Total time of execution
     */
    private String timeValue;

    public DbElasticReportWrapper(List<ElasticRecord> records, final long totalDocs, final String timeValue) {
        super(records, totalDocs);

        this.timeValue = timeValue;
    }

    DbElasticReportWrapper() {
        this(new ArrayList<>(), 0, null);
    }

    public String getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }
}
