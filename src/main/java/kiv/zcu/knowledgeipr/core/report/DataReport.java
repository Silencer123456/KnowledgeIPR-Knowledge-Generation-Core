package kiv.zcu.knowledgeipr.core.report;

import kiv.zcu.knowledgeipr.core.dbaccess.DbRecord;

import java.util.List;

/**
 * Contains the returned data ready to be sent to the client.
 * Plus contains summary etc.
 */
public class DataReport {
    private String summary = "Test summary";
    private List<DbRecord> records;

    public DataReport() {
    }

    public DataReport(List<DbRecord> records) {
        this.records = records;
    }

    public List<DbRecord> getRecords() {
        return records;
    }

    public String getSummary() {
        return summary;
    }
}
