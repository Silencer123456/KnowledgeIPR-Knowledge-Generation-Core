package kiv.zcu.knowledgeipr.core;

/**
 * Represents a single query
 */
public class Query {
    private String sourceType;
    private String filter;
    private String query;

    public String getSourceType() {
        return sourceType;
    }

    public String getFilter() {
        return filter;
    }

    public String getQuery() {
        return query;
    }

    /**
     * Checks if the query should be run selectively (If we want to restrict
     * the search to specific field)
     * @return true, if we want to restrict the search, else false
     */
    public boolean isSelectiveSearch() {
        return !filter.isEmpty();
    }
}
