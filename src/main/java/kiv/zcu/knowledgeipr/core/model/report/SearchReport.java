package kiv.zcu.knowledgeipr.core.model.report;

import java.util.List;

/**
 * Abstract class holding a list of data with various types.
 *
 * @param <T> The type of records to hold
 */
public abstract class SearchReport<T> {
    private String summary = "Data Report";
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
}
