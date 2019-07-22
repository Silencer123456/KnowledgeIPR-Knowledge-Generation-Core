package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DbManager;
import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class QueryRepository implements IRepository<QueryDto> {

    private DbManager dbManager;

    private QueryRunner runner;

    public QueryRepository(DbManager dbManager) {
        this.dbManager = dbManager;

        runner = new QueryRunner();
    }

    @Override
    public long add(QueryDto item) {
        return add(Collections.singletonList(item));
    }

    @Override
    public long add(Iterable<QueryDto> items) {
        long newId = -1;

        final Connection connection = dbManager.getConnection();
        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        String insertQuery = "INSERT INTO query (hash, rawQueryText, normalizedText) VALUES (?, ?, ?)";
        try {
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
        return null;
    }
}
