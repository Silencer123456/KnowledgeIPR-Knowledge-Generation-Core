package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.ElasticSearchReport;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;

public class DefaultElasticSearchStrategy extends SearchStrategy<Search, IElasticDataSearcher> {
    public DefaultElasticSearchStrategy(IElasticDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService);
    }

    @Override
    public ElasticSearchReport search(Search search) throws UserQueryException {
        dataSearcher.searchData(search.getQuery());
        return null;
    }
}
