package kiv.zcu.knowledgeipr.core.model.search;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryOptionsValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a client's query to be performed on the target database
 */
@JsonDeserialize(using = QueryDeserializer.class)
public class Query {
    private Map<String, String> filters;
    private Map<String, Map<String, Integer>> conditions;
    private QueryOptions options;

    private List<String> fields;

    public Query(Map<String, String> filters,
                 Map<String, Map<String, Integer>> conditions,
                 Map<String, Object> options) {
        this.filters = filters;
        this.conditions = conditions;
        this.options = new QueryOptions(options);
        this.fields = new ArrayList<>();
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public String getTextFilter() {
        return filters.get("$text");
    }

    public Map<String, Map<String, Integer>> getConditions() {
        return conditions;
    }

    public QueryOptions getOptions() {
        return options;
    }

    public void validate() throws QueryOptionsValidationException {
        if (filters == null) {
            throw new QueryOptionsValidationException("Missing filters.");
        }
        if (conditions == null) {
            conditions = new HashMap<>();
        }

        this.options.validate();
    }

    @Override
    public int hashCode() {
        int result = 17;
        //result = 31 * result + sourceType.hashCode();
        if (filters != null) {
            result = 31 * result + filters.hashCode();
        }
        if (conditions != null) {
            result = 31 * result + conditions.hashCode();
        }
        if (fields != null) {
            result = 31 * result + fields.hashCode();
        }

        return result;
    }
}
