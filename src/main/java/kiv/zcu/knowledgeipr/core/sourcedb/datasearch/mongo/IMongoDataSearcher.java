package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;

import java.util.List;

/**
 * Classes implementing this interface are able to run searches on the Mongo database using the provided
 *
 * @see Query instance.
 */
public interface IMongoDataSearcher extends IDataSearcher<MongoRecord> {

    /**
     * Runs an advanced search on the MongoDB database and returns a result set.
     * First a quick search is performed, doing an exact match search, which should be very fast when proper indexes are used.
     * This search is limited to just a few seconds of execution.
     * If no results are returned after the timeout, the second more thorough search is run with user
     * specified timeout.
     *
     * Returns empty list if
     *
     * @param search - The search instance
     * @return - Result list of <code>knowledgeipr.MongoRecord</code> instances.
     */
    List<MongoRecord> runSearchAdvanced(final Search search) throws UserQueryException, QueryExecutionException;

    /**
     * Runs a simple search on the MongoDB database. The simple search consists of a single search
     * which only finds results using an exact match if the text search is not specified.
     *
     * @param search - The search instance
     * @return - Result list of <code>knowledgeipr.MongoRecord</code> instances.
     * @throws UserQueryException             - If the user search is not in correct format
     */
    List<MongoRecord> runSearchSimple(final Search search) throws UserQueryException, QueryExecutionException;
}
