package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.model.search.SearchEngineName;

/**
 * Specifies the strategy of how the searches are performed.
 * Each search strategy can implement various process of searching (for example,
 * category search strategy first fetches confirmed results from the database etc.)
 *
 * The search strategy is responsible for the creation of reports.
 */
public abstract class SearchStrategy<T extends Search, V extends IDataSearcher> {

    /**
     * A database service class which manipulates the SQL database by implementing various functions.
     */
    //TODO: Use interface instead
    protected DbQueryService queryService;

    protected SearchEngineName searchEngineName;

    /**
     * Provides methods for searching the target database for data
     */
    protected V dataSearcher;

    public SearchStrategy(V dataSearcher, DbQueryService queryService, SearchEngineName searchEngineName) {
        this.dataSearcher = dataSearcher;
        this.queryService = queryService;
        this.searchEngineName = searchEngineName;
    }

    /**
     * Implements the execution of the search on the target database and gets a list of
     * results. Finally generates a report instance from the returned results.
     *
     * @param search - Search instance containing info necessary to perform the search
     * @return Created report from the retrieved results
     * @throws UserQueryException - In case the search is malformed
     */
    /**
     * Executes the search specification on the target database and gets a list of
     * results. Final report is generated from the returned results.
     *
     * @param searchSpecification - The specification of the search
     * @return - List of retrieved documents from the target database
     * @throws UserQueryException
     * @throws QueryExecutionException
     */
    public abstract SearchReport search(SearchSpecification<T> searchSpecification) throws UserQueryException, QueryExecutionException;

    /**
     * Saves the data report along with the search to the database to be used as cache,
     * so that next time the same search is performed, it is fetched from the database.
     *
     * @param search     - The Search instance to be saved to the database
     * @param searchReport - The MongoSearchReport instance to be saved to the database with the search
     */
    protected void cacheSearch(T search, SearchReport searchReport) {
        queryService.cacheQuery(search, searchReport, searchEngineName);
    }

    /**
     * Invalidates the cached searches from the database
     */
    public void invalidateCache() {
        queryService.invalidateCache();
    }

    public SearchEngineName getSearchEngineName() {
        return searchEngineName;
    }
}
