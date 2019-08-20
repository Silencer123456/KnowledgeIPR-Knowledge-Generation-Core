package kiv.zcu.knowledgeipr.core.database.specification;

import java.util.List;

public class SqlQuery {
    private List<Object> parameters;
    private String queryText;

    public SqlQuery(List<Object> parameters, String queryText) {
        this.parameters = parameters;
        this.queryText = queryText;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public String getQueryText() {
        return queryText;
    }
}
