package kiv.zcu.knowledgeipr.core.controller;

import kiv.zcu.knowledgeipr.core.dataaccess.DbRecord;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.IDataSearcher;
import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.query.Search;
import kiv.zcu.knowledgeipr.core.report.DataReport;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

import java.util.List;

public class DefaultSearchStrategy extends SearchStrategy {

    /**
     * A database service class which manipulates the SQL database. Mainly used for caching queries, reports...
     */
    private DbQueryService queryService;

    public DefaultSearchStrategy(IDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher);
        this.queryService = queryService;
    }

    /**
     * Initiates the execution of the query on the target database and gets a list of
     * results. Finally generates a report instance from the returned results.
     *
     * @param search - Search instance containing info necessary for searching
     * @return Created report from the retrieved results
     * @throws UserQueryException - In case the query is malformed
     */
    @Override
    public DataReport search(Search search) throws UserQueryException {
        DataReport report = queryService.getCachedReport(search);
        if (report != null) {
            return report;
        }

        List<DbRecord> records;
        if (search.isAdvancedSearch()) {
            records = dataSearcher.runSearchAdvanced(search.getQuery(), search.getPage(), search.getLimit());
        } else {
            records = dataSearcher.runSearchSimple(search.getQuery(), search.getPage(), search.getLimit());
        }

        report = new DataReport(records);

        queryService.cacheQuery(search, report);

        return report;
    }
}
