package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;

import java.util.ArrayList;
import java.util.List;

public abstract class DbReport<T extends IDbRecord> {

    private List<T> records;
    private long docsCount;

    public DbReport(List<T> records, long docsCount) {
        this.records = records;
        this.docsCount = docsCount;
    }

    DbReport() {
        this(new ArrayList<>(), 0);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getDocsCount() {
        return docsCount;
    }

    public void setDocsCount(long docsCount) {
        this.docsCount = docsCount;
    }
}
