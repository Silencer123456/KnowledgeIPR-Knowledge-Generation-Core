package kiv.zcu.knowledgeipr.core.model.report;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.MongoRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of returned search results of type MongoRecord.
 */
public class MongoSearchReport extends SearchReport<MongoRecord> {

    public MongoSearchReport() {
        super(new ArrayList<>());
    }

    public MongoSearchReport(List<MongoRecord> records) {
        super(records);
    }
}
