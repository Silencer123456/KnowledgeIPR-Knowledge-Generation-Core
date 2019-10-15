package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.ElasticSearchReport;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.model.search.SearchEngineName;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;

/**
 * Implements a default search strategy. It tries to get the cached report if exists and then performs the search,
 * retrieves the Elastic report and caches all the new searches to the database.
 */
public class DefaultElasticSearchStrategy extends SearchStrategy<Search, IElasticDataSearcher> {
    public DefaultElasticSearchStrategy(IElasticDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService, SearchEngineName.elastic);
    }

    @Override
    public SearchReport search(SearchSpecification<Search> searchSpecification) throws UserQueryException, QueryExecutionException {
        Search search = searchSpecification.getSearch();

        ElasticSearchReport report;
        //boolean useCache = (boolean) search.getQuery().getOptions().getOption(QueryOptions.QueryOption.USE_CACHE); // TODO move to Search class as helper method
        //if (useCache) {
        report = (ElasticSearchReport) queryService.getCachedReport(search, ElasticSearchReport.class);
        if (report != null) {
            return report;
        }
        //}

        DbElasticReportWrapper dbReport = dataSearcher.search(searchSpecification);

        report = new ElasticSearchReport(dbReport.getRecords(), dbReport.getDocsCount(), dbReport.getTimeValue(), dbReport.getSearchedIndexes());

        cacheSearch(search, report);

        return report;
    }
}
