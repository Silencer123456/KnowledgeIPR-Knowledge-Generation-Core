package kiv.zcu.knowledgeipr.core.knowledgedb.repository;


import kiv.zcu.knowledgeipr.core.knowledgedb.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.knowledgedb.specification.Specification;
import kiv.zcu.knowledgeipr.core.knowledgedb.specification.SqlQuery;
import kiv.zcu.knowledgeipr.core.knowledgedb.specification.SqlSpecification;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public abstract class BasicRepository<T> implements IRepository<T> {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    QueryRunner runner;

    private String tableName;

    BasicRepository(String tableName) {
        this.tableName = tableName;

        runner = new QueryRunner();
    }

    @Override
    public long add(T item) throws SQLException {
        return add(Collections.singletonList(item));
    }

    @Override
    public void removeAll() {
        String query = "DELETE FROM " + tableName;
        try {
            final Connection connection = DataSourceUtils.getConnection();
            runner.update(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This additional method has to be here because of the second parameter which
     * needs to be passed in order to infer the class type. Cannot select from a generic
     * type variable because of type erasure.
     *
     * @param specification - The Sql specification to be run
     * @param clazz         - T class type
     * @return - List of instances of type T
     */
    List<T> queryGeneric(Specification specification, Class<T> clazz) {
        final SqlSpecification sqlSpecification = (SqlSpecification) specification;

        BeanListHandler<T> beanListHandler = new BeanListHandler<>(clazz);
        try {
            final Connection connection = DataSourceUtils.getConnection();
            SqlQuery sqlQuery = sqlSpecification.toSqlQuery();

            return runner.query(connection, sqlQuery.getQueryText(), beanListHandler, sqlQuery.getParameters().toArray());
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
