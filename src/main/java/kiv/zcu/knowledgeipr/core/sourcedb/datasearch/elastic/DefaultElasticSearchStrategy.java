package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.ElasticSearchReport;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;

import java.util.List;

public class DefaultElasticSearchStrategy extends SearchStrategy<Search, IElasticDataSearcher> {
    public DefaultElasticSearchStrategy(IElasticDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService);
    }

    @Override
    public ElasticSearchReport search(Search search) throws UserQueryException, QueryExecutionException {
        List<ElasticRecord> records = dataSearcher.searchData(search.getQuery());


        ElasticSearchReport searchReport = new ElasticSearchReport(records);

        return searchReport;
    }
}
