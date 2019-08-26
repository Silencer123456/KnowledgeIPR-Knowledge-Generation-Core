package kiv.zcu.knowledgeipr.core.database.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.core.database.dbconnection.DataSourceUtils;
import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.database.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.database.mapper.QueryToQueryDtoMapper;
import kiv.zcu.knowledgeipr.core.database.mapper.ReportToReportDtoMapper;
import kiv.zcu.knowledgeipr.core.database.repository.IRepository;
import kiv.zcu.knowledgeipr.core.database.repository.QueryRepository;
import kiv.zcu.knowledgeipr.core.database.repository.ReferenceRepository;
import kiv.zcu.knowledgeipr.core.database.repository.ReportRepository;
import kiv.zcu.knowledgeipr.core.database.specification.QueryByHashCodeSpecification;
import kiv.zcu.knowledgeipr.core.database.specification.RecordsWithConfirmedCategorySpecification;
import kiv.zcu.knowledgeipr.core.database.specification.ReportsForQuerySpecification;
import kiv.zcu.knowledgeipr.core.report.SearchReport;
import kiv.zcu.knowledgeipr.core.search.Query;
import kiv.zcu.knowledgeipr.core.search.Search;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class enables interaction with SQL database. It provides various methods
 * manipulating the database tables and getting data from that database.
 */
public class DbQueryService {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private IRepository<QueryDto> queryRepository;
    private IRepository<ReportDto> reportsRepository;
    private IRepository<ReferenceDto> referenceRepository;

    public DbQueryService() {
        queryRepository = new QueryRepository();
        reportsRepository = new ReportRepository();
        referenceRepository = new ReferenceRepository();
    }

    /**
     * Retrieves a list of document records from the target database which are confirmed
     * to fall under the specified category.
     *
     * @param categoryName - The category for which to search document records.
     */
    public List<ReferenceDto> getConfirmedRecordsForCategory(String categoryName) {
        LOGGER.info("Retrieving confirmed records for category: " + categoryName);
        try {
            DataSourceUtils.startTransaction();

            List<ReferenceDto> referenceDtos = referenceRepository.query(new RecordsWithConfirmedCategorySpecification(categoryName));

            DataSourceUtils.commitAndClose();

            return referenceDtos;

        } catch (SQLException e) {
            LOGGER.warning("Could not retrieve confirmed records: " + e.getMessage());
            e.printStackTrace();

            DataSourceUtils.rollbackAndClose();
        }

        return Collections.emptyList();
    }

    /**
     * Creates a cached version of the query by storing it in the SQL database
     * under a unique hash key. A report is also saved to the database and
     * associated to the query.
     *
     * @param search - The search instance containing the query to be saved
     * @param report - The report to be saved and associated with the query
     */
    public void cacheQuery(Search search, SearchReport report) {
        LOGGER.info("Saving search " + search.getQuery().hashCode() + "; limit: " + search.getLimit() + "; page: " + search.getPage());
        try {
            QueryDto queryDto = new QueryToQueryDtoMapper().map(search.getQuery());

            if (getCachedReport(search) != null) {
                LOGGER.info("Report for search already exists, saving skipped");
                return;
            }

            DataSourceUtils.startTransaction();

            long queryId = queryRepository.add(queryDto);
            if (queryId == -1) {
                LOGGER.warning("Could not save search to database.");
            }
            queryDto.setId(queryId);

            ReportDto reportDto = new ReportToReportDtoMapper(search.getPage(), search.getLimit(), queryId).map(report);

            reportDto.setQueryId(queryId);
            long reportId = reportsRepository.add(reportDto);

            DataSourceUtils.commitAndClose();

        } catch (ObjectSerializationException | SQLException e) {
            LOGGER.warning("Query could not be saved: " + e.getMessage());
            e.printStackTrace();

            DataSourceUtils.rollbackAndClose();
        }
    }

    /**
     * Invalidates the existing database cache by removing everything
     * from report and query tables.
     */
    public void invalidateCache() {
        try {
            DataSourceUtils.startTransaction();

            reportsRepository.removeAll();
            queryRepository.removeAll();

            DataSourceUtils.commitAndClose();
        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.warning("Cache NOT INVALIDATED");
            e.printStackTrace();
        }
        LOGGER.info("Cache INVALIDATED");
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
     * Returns a report associated with the specified search, where page and limit match
     *
     * @param search - Search instance, for which to search reports
     * @return - Found cached report or null if nothing is found
     */
    // TODO: refactor, later instead of deserializing to report, use only the json
    public SearchReport getCachedReport(Search search) {
        SearchReport searchReport = null;
        try {
            DataSourceUtils.startTransaction();

            List<ReportDto> reportDtoList = reportsRepository.query(new ReportsForQuerySpecification(search.getQuery(), search.getPage(), search.getLimit()));
            if (reportDtoList == null || reportDtoList.isEmpty()) {
                DataSourceUtils.close();
                return null;
            }

            ReportDto reportDto = reportDtoList.get(0);
            try {
                searchReport = new ObjectMapper().readValue(reportDto.getReportText(), SearchReport.class);
                LOGGER.info("Cached report found for search: " + search.getQuery().hashCode() + ", page: " + search.getPage() + ", limit: " + search.getLimit());
            } catch (IOException e) {
                LOGGER.info("Cached report could not be parsed.");
                e.printStackTrace();
            }

            DataSourceUtils.close();

        } catch (SQLException e) {
            e.printStackTrace();

            DataSourceUtils.rollbackAndClose();
        }

        return searchReport;
    }
}
