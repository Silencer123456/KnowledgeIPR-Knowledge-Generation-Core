package kiv.zcu.knowledgeipr.core.database.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.core.database.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.database.repository.specification.QueryByHashCodeSpecification;
import kiv.zcu.knowledgeipr.core.database.repository.specification.ReportsForQuerySpecification;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.core.report.DataReport;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class enables access to the SQL database.
 */
public class DbQueryService {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private IRepository<QueryDto> queryRepository;
    private IRepository<ReportDto> reportsRepository;

    public DbQueryService() {
        queryRepository = new QueryRepository();
        reportsRepository = new ReportRepository();

    }

    /**
     * Saves query to the database and associates a report with it.
     *
     * @param query - The query to be saved
     * @param report - The report to be saved and associated to the query
     * @param limit - results limit
     * @param page page number
     */
    // TODO: add reports, create relationships; Accept DAO classes instead of DTO and use a mapper to convert them
    public void saveQuery(Query query, DataReport report, int limit, int page) {
        LOGGER.info("Saving query " + query.hashCode() + "; limit: " + limit + "; page: " + page);
        try {
            QueryDto queryDto = new QueryDto(query.hashCode(), SerializationUtils.serializeObject(query), "test");

            if (getReportForQuery(query, page, limit) != null) {
                LOGGER.info("Report for query already exists, saving skipped");
                return;
            }

            DataSourceUtils.startTransaction();

            long queryId = queryRepository.add(queryDto);
            if (queryId == -1) {
                LOGGER.warning("Could not save query to database.");
            }
            queryDto.setId(queryId);

            ReportDto reportDto = new ReportDto(queryId, limit, SerializationUtils.serializeObject(report), null, null, page);

            reportDto.setQueryId(queryId);
            long reportId = reportsRepository.add(reportDto);

            DataSourceUtils.commitAndClose();

        } catch (ObjectSerializationException | SQLException e) {
            LOGGER.warning("Query could not be saved: " + e.getMessage());
            e.printStackTrace();

            DataSourceUtils.rollbackAndClose();
        }
    }

    public Query getByHash(int hashCode) {
        Query resultQuery = null;
        List<QueryDto> queryDtoList = queryRepository.query(new QueryByHashCodeSpecification(hashCode));
        if (queryDtoList.size() >= 1) {
            QueryDto tmp = queryDtoList.get(0);
            resultQuery = new Query("", new HashMap<>(), new HashMap<>(), new HashMap<>());
        }

        return resultQuery;
    }

    /**
     * Returns a report associated with the specified query, where page and limit match
     *
     * @param query - Query, for which to search reports
     * @param page  - Results page
     * @param limit - Results count limit
     * @return
     */
    // TODO: refactor, later instead of deserializing to report, use only the json
    public DataReport getReportForQuery(Query query, int page, int limit) {
        List<ReportDto> reportDtoList = reportsRepository.query(new ReportsForQuerySpecification(query, page, limit));
        if (reportDtoList == null || reportDtoList.isEmpty()) {
            return null;
        }

        ReportDto reportDto = reportDtoList.get(0);
        try {
            DataReport dataReport = new ObjectMapper().readValue(reportDto.getReportText(), DataReport.class);
            LOGGER.info("Cached report found for query: " + query.hashCode() + ", page: " + page + ", limit: " + limit);
            return dataReport;
        } catch (IOException e) {
            LOGGER.info("No cached report found");
            e.printStackTrace();
            return null;
        }
    }
}
