package kiv.zcu.knowledgeipr.core.database.specification;

import kiv.zcu.knowledgeipr.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ReportsForQuerySpecification implements SqlSpecification {

    private final Query query;
    private int page;
    private int limit;

    public ReportsForQuerySpecification(final Query query, final int page, final int limit) {
        this.query = query;
        this.page = page;
        this.limit = limit;
    }

    // TODO: replace hardcoded strings
    @Override
    public SqlQuery toSqlQuery() {
        List<Object> parameters = new ArrayList<>();
        parameters.add(query.hashCode());
        parameters.add(page);
        parameters.add(limit);
        return new SqlQuery(parameters, "SELECT * FROM report INNER JOIN query ON report.queryId = query.queryId " +
                "WHERE query.hash = ? AND page = ? AND docsPerPage = ?");
    }
}
