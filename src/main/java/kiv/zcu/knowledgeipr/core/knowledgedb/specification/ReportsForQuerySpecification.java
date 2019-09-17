package kiv.zcu.knowledgeipr.core.knowledgedb.specification;

import kiv.zcu.knowledgeipr.core.model.search.Query;

import java.util.ArrayList;
import java.util.List;

public class ReportsForQuerySpecification implements SqlSpecification {

    private final Query query;
    private int page;
    private int limit;
    private String searchEngineName;
    private String sourceType;

    public ReportsForQuerySpecification(final Query query, final int page, final int limit, final String searchEngineName,
                                        final String sourceType) {
        this.query = query;
        this.page = page;
        this.limit = limit;
        this.searchEngineName = searchEngineName;
        this.sourceType = sourceType;
    }

    // TODO: replace hardcoded strings
    @Override
    public SqlQuery toSqlQuery() {
        List<Object> parameters = new ArrayList<>();
        parameters.add(query.hashCode());
        parameters.add(page);
        parameters.add(limit);
        parameters.add(searchEngineName);
        parameters.add(sourceType);
        return new SqlQuery(parameters, "SELECT * FROM report INNER JOIN query ON report.queryId = query.queryId " +
                "WHERE query.hash = ? AND page = ? AND docsPerPage = ? AND dbEngine = ? AND sourceType = ?");
    }
}
