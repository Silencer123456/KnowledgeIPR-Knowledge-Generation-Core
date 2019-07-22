package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DbManager;
import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

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
    public long add(ReportDto item) {
        return add(Collections.singletonList(item));
    }

    @Override
    public long add(Iterable<ReportDto> items) {
        long newId = -1;

        final Connection connection = dbManager.getConnection();
        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        String insertQuery = "INSERT INTO report (queryId, docsPerPage, reportText, page) VALUES (?, ?, ?, ?)";
        try {
            for (ReportDto report : items) {
                newId = runner.insert(connection, insertQuery, scalarHandler,
                        report.getQuery().getId(), report.getDocsPerPage(), report.getReportText(), report.getPage());
                // TODO: check if success ...

                // TODO: insert to references + reportreferences table

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return newId;
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

    @Override
    public ReportDto getById(int id) {
        return null;
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
