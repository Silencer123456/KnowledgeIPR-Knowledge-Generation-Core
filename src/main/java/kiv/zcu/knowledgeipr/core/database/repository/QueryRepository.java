package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.database.specification.Specification;
import kiv.zcu.knowledgeipr.core.database.specification.SqlQuery;
import kiv.zcu.knowledgeipr.core.database.specification.SqlSpecification;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryRepository implements IRepository<QueryDto> {

    private static final String QUERY_TABLE_NAME = "query";

    private QueryRunner runner;

    public QueryRepository() {
        runner = new QueryRunner();
    }

    @Override
    public long add(QueryDto item) {
        return add(Collections.singletonList(item));
    }

    @Override
    public long add(Iterable<QueryDto> items) {
        long newId = -1;

        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        String insertQuery = "INSERT INTO " + QUERY_TABLE_NAME + " (hash, rawQueryText, normalizedText) VALUES (?, ?, ?)";
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
    public void removeAll() {
        String query = "DELETE FROM " + QUERY_TABLE_NAME;
        try {
            final Connection connection = DataSourceUtils.getConnection();
            runner.update(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public QueryDto getById(int id) {
        return null;
    }

    @Override
    public List<QueryDto> query(Specification specification) {
        final SqlSpecification sqlSpecification = (SqlSpecification) specification;

        BeanListHandler<QueryDto> beanListHandler
                = new BeanListHandler<>(QueryDto.class);

        SqlQuery sqlQuery = sqlSpecification.toSqlQuery();

        List<QueryDto> queriesList = new ArrayList<>();
        try {
            final Connection connection = DataSourceUtils.getConnection();
            queriesList = runner.query(connection, sqlQuery.getQueryText(), beanListHandler, sqlQuery.getParameters().toArray());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return queriesList;
    }
}
