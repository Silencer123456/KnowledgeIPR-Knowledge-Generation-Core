package kiv.zcu.knowledgeipr.core.knowledgedb.repository;

import kiv.zcu.knowledgeipr.core.knowledgedb.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.knowledgedb.specification.Specification;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ReportRepository extends BasicRepository<ReportDto> {

    private static final String TABLE_NAME = "report";

    public ReportRepository() {
        super(TABLE_NAME);
    }

    @Override
    public long add(Iterable<ReportDto> items) {
        long newId = -1;

        ScalarHandler<Long> scalarHandler = new ScalarHandler<>();

        String insertQuery = "INSERT INTO " + TABLE_NAME + " (queryId, docsPerPage, reportText, page) VALUES (?, ?, ?, ?)";
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
    public ReportDto getById(int id) {
        return null;
    }

    // TODO: Handle better the exception handling
    @Override
    public List<ReportDto> query(Specification specification) {
        return queryGeneric(specification, ReportDto.class);
    }
}
