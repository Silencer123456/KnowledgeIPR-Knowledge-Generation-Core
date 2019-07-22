package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DbManager;
import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.database.repository.specification.QueryByHashCodeSpecification;
import kiv.zcu.knowledgeipr.core.database.repository.specification.ReportsForQuerySpecification;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.core.report.DataReport;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class enables access to the SQL database.
 */
public class DbQueryHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private IRepository<QueryDto> queryRepository;
    private IRepository<ReportDto> reportsRepository;

    public DbQueryHandler() {
        try {
            DbManager dbManager = new DbManager();
            queryRepository = new QueryRepository(dbManager);
            reportsRepository = new ReportRepository(dbManager);

        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.severe("Connection to the database could not be established");
            e.printStackTrace();
        }
    }

    public void checkCache(QueryDto query, int page, int limit) {
        //reportsRepository.query(new ReportsForQuerySpecification(query));
    }

    // TODO: add reports, create relationships; Accept DAO classes instead of DTO and use a mapper to convert them
    public void saveQuery(QueryDto query, ReportDto reportDto) {
        long queryId = queryRepository.add(query);
        if (queryId == -1) {
            LOGGER.warning("Could not save query to database.");
        }
        query.setId(queryId);

        reportDto.setQuery(query);
        long reportId = reportsRepository.add(reportDto);



        //TODO: check success... log report.....


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

    public List<DataReport> getReportsForQuery(Query query) {
        // get query by hash from DB
        //Query q = getByHash(query.hashCode());

        List<ReportDto> reportDtoList = reportsRepository.query(new ReportsForQuerySpecification(query));

        return null;
    }
}
