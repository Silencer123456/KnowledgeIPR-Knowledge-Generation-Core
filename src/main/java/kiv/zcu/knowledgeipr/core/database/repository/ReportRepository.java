package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.database.specification.Specification;
import kiv.zcu.knowledgeipr.core.database.specification.SqlQuery;
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
    public long add(ReportDto item) {
        return add(Collections.singletonList(item));
    }

    @Override
    public long add(Iterable<ReportDto> items) {
        long newId = -1;

        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        String insertQuery = "INSERT INTO report (queryId, docsPerPage, reportText, page) VALUES (?, ?, ?, ?)";
        try {
            final Connection connection = DataSourceUtils.getConnection();
            for (ReportDto report : items) {
                newId = runner.insert(connection, insertQuery, scalarHandler,
                        report.getQueryId(), report.getDocsPerPage(), report.getReportText(), report.getPage());
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
    public void removeAll() {
        String query = "DELETE FROM report";
        try {
            final Connection connection = DataSourceUtils.getConnection();
            runner.update(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ReportDto getById(int id) {
        return null;
    }

    // TODO: Handle better the exception handling
    @Override
    public List<ReportDto> query(Specification specification) {
        final SqlSpecification sqlSpecification = (SqlSpecification) specification;

        BeanListHandler<ReportDto> beanListHandler = new BeanListHandler<>(ReportDto.class);
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
