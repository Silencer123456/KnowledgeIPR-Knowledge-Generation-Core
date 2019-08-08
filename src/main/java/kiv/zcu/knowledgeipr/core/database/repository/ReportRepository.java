package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.database.specification.Specification;
import kiv.zcu.knowledgeipr.core.database.specification.SqlSpecification;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ReportRepository implements IRepository<ReportDto> {

    private QueryRunner runner;

    public ReportRepository() {

        runner = new QueryRunner();
    }

    @Override
    public long add(Connection connection, ReportDto item) {
        return add(connection, Collections.singletonList(item));
    }

    @Override
    public long add(Connection connection, Iterable<ReportDto> items) {
        long newId = -1;

        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        String insertQuery = "INSERT INTO report (queryId, docsPerPage, reportText, page) VALUES (?, ?, ?, ?)";
        try {
            for (ReportDto report : items) {
                newId = runner.insert(connection, insertQuery, scalarHandler,
                        report.getQueryId(), report.getDocsPerPage(), report.getReportText(), report.getPage());
                // TODO: check if success ...

                // TODO: insert to references + reportreferences table

            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return newId;
    }

    @Override
    public void update(Connection connection, ReportDto item) {

    }

    @Override
    public void remove(Connection connection, ReportDto item) {

    }

    @Override
    public void remove(Connection connection, Specification specification) {

    }

    @Override
    public void removeAll(Connection connection) {
        String query = "DELETE FROM report";
        try {
            runner.update(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ReportDto getById(int id) {
        return null;
    }

    // TODO: FIX CONNECTION CLOSING!!!!
    @Override
    public List<ReportDto> query(Connection connection, Specification specification) {
        final SqlSpecification sqlSpecification = (SqlSpecification) specification;

        BeanListHandler<ReportDto> beanListHandler = new BeanListHandler<>(ReportDto.class);
        try {
            List<ReportDto> reportsList = runner.query(connection, sqlSpecification.toSqlQuery(), beanListHandler);
            connection.close();
            return reportsList;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
