package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DbManager;
import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import org.apache.commons.dbutils.QueryRunner;

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
    public void add(QueryDto item) {
        add(Collections.singletonList(item));
    }

    @Override
    public void add(Iterable<QueryDto> items) {
        final Connection connection = dbManager.getConnection();

        String insertQuery = "INSERT INTO query (hash, rawQueryText, normalizedText) VALUES (?, ?, ?)";
        try {
            for (QueryDto query : items) {
                int numRowsInserted = runner.update(connection, insertQuery, query.getHash(), query.getRawText(), query.getNormalizedText());
                // TODO: check if success ...
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public List<QueryDto> query(Specification specification) {
        return null;
    }
}
