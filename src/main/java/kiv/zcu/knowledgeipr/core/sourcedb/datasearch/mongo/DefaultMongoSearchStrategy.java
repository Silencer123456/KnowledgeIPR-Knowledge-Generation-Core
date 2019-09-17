package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.MongoSearchReport;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.model.search.SearchEngineName;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;

import java.util.List;

public class DefaultMongoSearchStrategy extends SearchStrategy<Search, IMongoDataSearcher> {

    public DefaultMongoSearchStrategy(IMongoDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService, SearchEngineName.mongo);
    }

    @Override
    public SearchReport search(SearchSpecification<Search> searchSpecification) throws UserQueryException, QueryExecutionException {
        Search search = searchSpecification.getSearch();

        MongoSearchReport report = (MongoSearchReport) queryService.getCachedReport(search, MongoSearchReport.class);
        if (report != null) {
            return report;
        }

        List<MongoRecord> records;
        if (search.isAdvancedSearch()) {
            records = dataSearcher.runSearchAdvanced(search);
        } else {
            records = dataSearcher.runSearchSimple(search);
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
