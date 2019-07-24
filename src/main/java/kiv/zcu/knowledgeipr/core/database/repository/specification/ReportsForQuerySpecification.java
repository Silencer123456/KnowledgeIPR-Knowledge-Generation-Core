package kiv.zcu.knowledgeipr.core.database.repository.specification;

import kiv.zcu.knowledgeipr.core.query.Query;

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
    public String toSqlQuery() {
        return String.format("SELECT * FROM %1$s INNER JOIN %2$s ON report.queryId = query.queryId " +
                        "WHERE query.hash = %3$s AND page = %4$s AND docsPerPage = %5$s",
                "report",
                "query",
                query.hashCode(),
                page,
                limit);
    }
}
