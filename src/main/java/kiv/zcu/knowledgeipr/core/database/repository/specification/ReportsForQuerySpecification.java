package kiv.zcu.knowledgeipr.core.database.repository.specification;

import kiv.zcu.knowledgeipr.core.query.Query;

public class ReportsForQuerySpecification implements SqlSpecification {

    private final Query query;

    public ReportsForQuerySpecification(final Query query) {
        this.query = query;
    }

    // TODO: Add checks for page, ...
    @Override
    public String toSqlQuery() {
        return String.format("SELECT * FROM %1$s INNER JOIN %2$s ON report.queryId = query.queryId " +
                        "WHERE query.hash = %3$s",
                "report",
                "query",
                query.hashCode());
    }
}
