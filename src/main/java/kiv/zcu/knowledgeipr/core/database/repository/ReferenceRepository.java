package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.database.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.database.specification.Specification;
import kiv.zcu.knowledgeipr.core.database.specification.SqlQuery;
import kiv.zcu.knowledgeipr.core.database.specification.SqlSpecification;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class ReferenceRepository implements IRepository<ReferenceDto> {

    private QueryRunner runner;

    public ReferenceRepository() {

        runner = new QueryRunner();
    }

    @Override
    public long add(ReferenceDto item) {
        return 0;
    }

    @Override
    public long add(Iterable<ReferenceDto> items) {
        return 0;
    }

    @Override
    public void update(ReferenceDto item) {

    }

    @Override
    public void remove(ReferenceDto item) {

    }

    @Override
    public void remove(Specification specification) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public ReferenceDto getById(int id) {
        return null;
    }

    @Override
    public List<ReferenceDto> query(Specification specification) {
        final SqlSpecification sqlSpecification = (SqlSpecification) specification;

        BeanListHandler<ReferenceDto> beanListHandler = new BeanListHandler<>(ReferenceDto.class);
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
