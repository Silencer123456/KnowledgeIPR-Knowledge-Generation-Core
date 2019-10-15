package kiv.zcu.knowledgeipr.core.model.report;

import java.util.List;

/**
 * Abstract class holding a list of data with various types.
 *
 * Note: This class is serialized to JSON, so all the fields have
 * to remain global
 *
 * @param <T> The type of records to hold
 */
public abstract class SearchReport<T> {
    private String summary = "Data Report";

    /**
     * The number of documents returned
     */
    private long docsCount;

    private List<String> searchedIndexes;

    /**
     * Total time of execution of the query
     */
    private String executionTime;

    /**
     * The data assigned to this report
     */
    protected List<T> data;


    public SearchReport(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public String getSummary() {
        return summary;
    }

    public long getDocsCount() {
        return docsCount;
    }

    public void setDocsCount(final long docsCount) {
        this.docsCount = docsCount;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    public List<String> getSearchedIndexes() {
        return searchedIndexes;
    }

    public void setSearchedIndexes(List<String> searchedIndexes) {
        this.searchedIndexes = searchedIndexes;
    }
}
