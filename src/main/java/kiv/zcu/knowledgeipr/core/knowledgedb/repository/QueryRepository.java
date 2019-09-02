package kiv.zcu.knowledgeipr.core.knowledgedb.repository;

import kiv.zcu.knowledgeipr.core.knowledgedb.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.knowledgedb.specification.Specification;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class QueryRepository extends BasicRepository<QueryDto> {

    private static final String TABLE_NAME = "query";

    public QueryRepository() {
        super(TABLE_NAME);
    }

    @Override
    public long add(Iterable<QueryDto> items) {
        long newId = -1;

        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        String insertQuery = "INSERT INTO " + TABLE_NAME + " (hash, rawQueryText, normalizedText) VALUES (?, ?, ?)";
        try {
            final Connection connection = DataSourceUtils.getConnection();

            for (QueryDto query : items) {
                newId = runner.insert(connection, insertQuery, scalarHandler,
                        query.getHash(), query.getRawText(), query.getNormalizedText());
                // TODO: check if success ...
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newId;
    }

    @Override
    public void update(QueryDto item) {

    }

    @Override
    public void remove(QueryDto item) {

    }

    @Override
    public void remove(Specification specification) {

    }

    @Override
    public QueryDto getById(int id) {
        return null;
    }

    @Override
    public List<QueryDto> query(Specification specification) {
        return queryGeneric(specification, QueryDto.class);
    }
}
