package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.report.SearchReport;
import kiv.zcu.knowledgeipr.core.search.Search;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

import java.util.List;

public class DefaultSearchStrategy extends SearchStrategy<Search, IMongoDataSearcher> {

    public DefaultSearchStrategy(IMongoDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchReport search(Search search) throws UserQueryException {
        SearchReport report = queryService.getCachedReport(search);
        if (report != null) {
            return report;
        }

        List<MongoRecord> records;
        if (search.isAdvancedSearch()) {
            records = dataSearcher.runSearchAdvanced(search.getQuery(), search.getPage(), search.getLimit());
        } else {
            records = dataSearcher.runSearchSimple(search.getQuery(), search.getPage(), search.getLimit());
        }

        report = new SearchReport(records);

        cacheSearch(search, report);

        return report;
    }
}
