package kiv.zcu.knowledgeipr.core.model.report;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.ElasticRecord;

import java.util.List;

public class ElasticSearchReport implements SearchReport {

    private String summary = "Data Report";
    private List<ElasticRecord> elasticRecords;

    public ElasticSearchReport(List<ElasticRecord> elasticRecords) {
        this.elasticRecords = elasticRecords;
    }

    public List<ElasticRecord> getElasticRecords() {
        return elasticRecords;
    }
}
