package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;

import java.util.List;

/**
 * Classes implementing this interface are able to run searches on the Mongo database using the provided
 *
 * @see Query instance.
 */
public interface IMongoDataSearcher extends IDataSearcher {

    /**
     * Runs an advanced search on the MongoDB database and returns a result set.
     * First a quick search is performed, doing an exact match search, which should be very fast when proper indexes are used.
     * This search is limited to just a few seconds of execution.
     * If no results are returned after the timeout, the second more thorough search is run with user
     * specified timeout.
     *
     * Returns empty list if
     *
     * @param query - knowledgeipr.Query to be run
     * @param page  - Page to return
     * @param limit - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.MongoRecord</code> instances.
     */
    List<MongoRecord> runSearchAdvanced(Query query, int page, final int limit) throws MongoQueryException, UserQueryException, MongoExecutionTimeoutException;

    /**
     * Runs a simple search on the MongoDB database. The simple search consists of a single search
     * which only finds results using an exact match if the text search is not specified.
     *
     * @param query - Query to be run
     * @param page  - Page to return
     * @param limit - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.MongoRecord</code> instances.
     * @throws MongoQueryException            - If there is a problem with a search execution
     * @throws UserQueryException             - If the user search is not in correct format
     * @throws MongoExecutionTimeoutException - If the timeout is reached without no results returned
     */
    List<MongoRecord> runSearchSimple(Query query, int page, final int limit) throws MongoQueryException, UserQueryException, MongoExecutionTimeoutException;

    /**
     * Searches for document records identified by the references.
     *
     * @param references - The list of references containing urls of the documents to retrieve
     * @return - The list of documents from Mongo associated with the references
     */
    List<MongoRecord> searchByReferences(List<ReferenceDto> references);
}
