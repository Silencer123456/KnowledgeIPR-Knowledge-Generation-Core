package kiv.zcu.knowledgeipr.core;

import java.util.Map;

/**
 * Represents a single query
 */
public class Query {
    private String sourceType;
    private Map<String, String> filters;

    public String getSourceType() {
        return sourceType;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public String getTextQuery() {
        return filters.get("text");
    }
}
