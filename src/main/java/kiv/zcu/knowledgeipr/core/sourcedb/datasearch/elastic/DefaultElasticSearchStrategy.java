package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
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
    public ElasticSearchReport search(Search search) throws UserQueryException, QueryExecutionException {
        ElasticSearchReport report = (ElasticSearchReport) queryService.getCachedReport(search, ElasticSearchReport.class);
        if (report != null) {
            return report;
        }

        DbElasticReport dbReport = dataSearcher.searchData(search);

        report = new ElasticSearchReport(dbReport.getRecords(), dbReport.getDocsCount());

        cacheSearch(search, report);

        return report;
    }
}
