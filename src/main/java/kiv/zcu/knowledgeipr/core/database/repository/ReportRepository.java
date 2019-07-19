package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DbManager;
import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ReportRepository implements IRepository<ReportDto> {

    private DbManager dbManager;

    private QueryRunner runner;

    public ReportRepository(DbManager dbManager) {
        this.dbManager = dbManager;

        runner = new QueryRunner();
    }

    @Override
    public void add(ReportDto item) {
        add(Collections.singletonList(item));
    }

    @Override
    public void add(Iterable<ReportDto> items) {

    }

    @Override
    public void update(ReportDto item) {

    }

    @Override
    public void remove(ReportDto item) {

    }

    @Override
    public void remove(Specification specification) {

    }

    // TODO: Handle better the exception handling
    @Override
    public List<ReportDto> query(Specification specification) {
        final SqlSpecification sqlSpecification = (SqlSpecification) specification;

        final Connection connection = dbManager.getConnection();

        BeanListHandler<ReportDto> beanListHandler = new BeanListHandler<>(ReportDto.class);
        try {
            return runner.query(connection, sqlSpecification.toSqlQuery(), beanListHandler);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
