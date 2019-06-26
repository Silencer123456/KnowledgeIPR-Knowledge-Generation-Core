package kiv.zcu.knowledgeipr.core;

import java.util.Map;

/**
 * Represents a single query
 */
public class Query {
    private String sourceType;
    private Map<String, String> filters;

    //private String filter;
    //private String query;

    public String getSourceType() {
        return sourceType;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public String getTextQuery() {
        return filters.get("text");
    }


    //    public String getFilter() {
//        return filter;
//    }
//
//    public String getTextQuery() {
//        return query;
//    }
//
//    /**
//     * Checks if the query should be run selectively (If we want to restrict
//     * the search to specific field)
//     * @return true, if we want to restrict the search, else false
//     */
//    public boolean isSelectiveSearch() {
//        if (filter == null) return false;
//        return !filter.isEmpty();
//    }
}
