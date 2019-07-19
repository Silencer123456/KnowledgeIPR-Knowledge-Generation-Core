package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;

public class ReportsForQuerySpecification implements SqlSpecification {

    private final QueryDto query;

    public ReportsForQuerySpecification(final QueryDto query) {
        this.query = query;
    }

    // TODO: Add checks for page, ...
    @Override
    public String toSqlQuery() {
        return "select * from report \n" +
                "inner join query\n" +
                "on report.queryId = query.queryId\n" +
                "where query.rawQueryText = \"afdafds\"";
    }
}
