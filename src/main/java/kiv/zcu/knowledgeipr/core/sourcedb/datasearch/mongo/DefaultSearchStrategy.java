package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.MongoSearchReport;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;

import java.util.List;

public class DefaultSearchStrategy extends SearchStrategy<Search, IMongoDataSearcher> {

    public DefaultSearchStrategy(IMongoDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoSearchReport search(Search search) throws UserQueryException, QueryExecutionException {
        MongoSearchReport report = (MongoSearchReport) queryService.getCachedReport(search, MongoSearchReport.class);
        if (report != null) {
            return report;
        }

        List<MongoRecord> records;
        if (search.isAdvancedSearch()) {
            records = dataSearcher.runSearchAdvanced(search.getQuery(), search.getPage(), search.getLimit());
        } else {
            records = dataSearcher.runSearchSimple(search.getQuery(), search.getPage(), search.getLimit());
        }

        for (MongoRecord record : records) {
            //TODO: Removes the id field from the document so it is not returned back to the user. !!! TMP solution
            record.getDocument().remove("_id");
        }

        report = new MongoSearchReport(records);

        cacheSearch(search, report);

        return report;
    }
}
