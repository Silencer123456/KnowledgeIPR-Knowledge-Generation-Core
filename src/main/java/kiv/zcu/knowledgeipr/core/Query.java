package kiv.zcu.knowledgeipr.core;

import kiv.zcu.knowledgeipr.rest.exception.QueryOptionsValidationException;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Map;

/**
 * Represents a single query
 */
@JsonDeserialize(using = QueryDeserializer.class)
public class Query {
    private String sourceType;
    private Map<String, String> filters;
    private Map<String, Map<String, Integer>> conditions;
    private QueryOptions options;

    public Query(String sourceType, Map<String, String> filters, Map<String, Map<String, Integer>> conditions, Map<String, Object> options) {
        this.sourceType = sourceType;
        this.filters = filters;
        this.conditions = conditions;
        this.options = new QueryOptions(options);
    }

    public String getSourceType() {
        return sourceType;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public String getTextQuery() {
        return filters.get("text");
    }

    public Map<String, Map<String, Integer>> getConditions() {
        return conditions;
    }

    public QueryOptions getOptions() {
        return options;
    }

    public void validate() throws QueryOptionsValidationException {
        this.options.validate();
    }
}
