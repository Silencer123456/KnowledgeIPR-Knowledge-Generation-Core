package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import kiv.zcu.knowledgeipr.core.dataaccess.IDataSearcher;
import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.report.SearchReport;
import kiv.zcu.knowledgeipr.core.search.Search;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

/**
 * Specifies the strategy of how the searches are performed.
 * Each search strategy can implement various process of searching (for example,
 * category search strategy first fetches confirmed results from the database etc.)
 */
public abstract class SearchStrategy<T extends Search, V extends IDataSearcher> {

    /**
     * A database service class which manipulates the SQL database by implementing various functions.
     */
    //TODO: Use interface instead
    DbQueryService queryService;

    /**
     * Provides methods for searching the target database for data
     */
    V dataSearcher;

    SearchStrategy(V dataSearcher, DbQueryService queryService) {
        this.dataSearcher = dataSearcher;
        this.queryService = queryService;
    }

    /**
     * Implements the execution of the search on the target database and gets a list of
     * results. Finally generates a report instance from the returned results.
     *
     * @param search - Search instance containing info necessary to perform the search
     * @return Created report from the retrieved results
     * @throws UserQueryException - In case the search is malformed
     */
    public abstract SearchReport search(T search) throws UserQueryException;

    /**
     * Saves the data report along with the search to the database to be used as cache,
     * so that next time the same search is performed, it is fetched from the database.
     *
     * @param search     - The Search instance to be saved to the database
     * @param searchReport - The SearchReport instance to be saved to the database with the search
     */
    void cacheSearch(T search, SearchReport searchReport) {
        queryService.cacheQuery(search, searchReport);
    }

    /**
     * Invalidates the cached searches from the database
     */
    public void invalidateCache() {
        queryService.invalidateCache();
    }
}
