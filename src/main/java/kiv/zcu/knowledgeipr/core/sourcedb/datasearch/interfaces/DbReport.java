package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic abstract class encapsulating data returned from the target database along
 * with other information.
 * <p>
 * Serves mainly as a transport object
 *
 * @param <T> - The concrete database record to be stored
 */
public abstract class DbReport<T extends IDbRecord> {

    /**
     * List of stored records of type IDbRecord
     */
    private List<T> records;
    /**
     * Total number of hits(documents) returned from the query.
     */
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
