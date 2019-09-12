package kiv.zcu.knowledgeipr.core.model.report;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.ElasticRecord;

import java.util.List;

/**
 * Report for data coming from ElasticSearch. Holds a list of ElasticRecord instances.
 */
public class ElasticSearchReport extends SearchReport<ElasticRecord> {

    public ElasticSearchReport(List<ElasticRecord> elasticRecords) {
        super(elasticRecords);
    }
}
