package kiv.zcu.knowledgeipr.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.api.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryOptionsValidationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.api.response.BaseResponse;
import kiv.zcu.knowledgeipr.api.response.SearchResponse;
import kiv.zcu.knowledgeipr.api.response.StatusResponse;
import kiv.zcu.knowledgeipr.api.response.WordNetResponse;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import javax.ws.rs.*;
import java.io.IOException;

/**
 * Service for handling incoming REST requests
 */
public abstract class SearchService<T extends IDataSearcher> {

    private DataAccessController dataAccessController;

    private SearchStrategy<Search, T> searchStrategy;

    public SearchService(DataAccessController dataAccessController, SearchStrategy<Search, T> searchStrategy) {
        this.dataAccessController = dataAccessController;
        this.searchStrategy = searchStrategy;
    }

    /**
     * Accepts a search, processes it and returns a set of results as JSON to the caller.
     *
     * @param page      - page of results to return
     * @param queryJson - request search json
     * @return JSON response
     * @throws ApiException - In case of user api errors
     */
    @POST
    @Logged
    @Path("/")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response search(@QueryParam("page") int page, String queryJson) throws ApiException, ObjectSerializationException {
        isPageValid(page);

        Query query;
        try {
            query = deserializeQuery(queryJson);

        } catch (IOException | QueryOptionsValidationException e) {
            e.printStackTrace();
            throw new ApiException(e.getMessage());
        }

        return processQueryInit(query, page, true);
    }

    /**
     * Runs a search on the database for a specific owner of patent in the specified year.
     *
     * @param page      - Page of the returned results
     * @param ownerName - Name of the owner for whom to search the patents
     * @param year - Year in which to include patents
     * @return
     * @throws ApiException
     */
    @POST
    @Logged
    @Path("/owners")
    @Consumes("application/json")
    @Produces("application/json")
    public abstract javax.ws.rs.core.Response ownersSearch(@QueryParam("page") int page,
                                                           @QueryParam("owner") String ownerName,
                                                           @QueryParam("year") int year)
            throws ApiException, ObjectSerializationException;

    /**
     * Fetches patent data about a specified patent number.
     *
     * @param patentNumber - The number of the patent
     * @return response with patent data with the specified patent number
     * @throws ApiException
     * @throws ObjectSerializationException
     */
    @POST
    @Logged
    @Path("/number")
    @Consumes("application/json")
    @Produces("application/json")
    public abstract javax.ws.rs.core.Response patentNumberSearch(@QueryParam("number") String patentNumber)
            throws ApiException, ObjectSerializationException;

    @GET
    @Logged
    @Path("/synonyms/{word}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getSynonymsForWord(@PathParam("word") String word) throws ObjectSerializationException {
        WordNetResponse response = dataAccessController.getSynonyms(word);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/hypernyms/{word}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getHypernymsForWord(@PathParam("word") String word) throws ObjectSerializationException {
        WordNetResponse response = dataAccessController.getHypernyms(word);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/hyponyms/{word}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getHyponymsForWord(@PathParam("word") String word) throws ObjectSerializationException {
        WordNetResponse response = dataAccessController.getHyponyms(word);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/antonyms/{word}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getAntonymsForWord(@PathParam("word") String word) throws ObjectSerializationException {
        WordNetResponse response = dataAccessController.getAntonyms(word);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Logged
    @Path("/invalidateCache")
    @Produces("application/json")
    public javax.ws.rs.core.Response invalidateCache() throws ObjectSerializationException {
        dataAccessController.invalidateCache(searchStrategy);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(new BaseResponse(StatusResponse.SUCCESS, "OK"))).build();
    }

    @POST
    @Logged
    @Path("/limited/{limit}")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response queryWithLimit(@PathParam("limit") int limit, String queryJson) throws ApiException, ObjectSerializationException {
        if (limit > 1000) {
            throw new ApiException("Limit cannot exceed 1000");
        }

        Query query;
        try {
            query = deserializeQuery(queryJson);

        } catch (IOException | QueryOptionsValidationException e) {
            e.printStackTrace();
            throw new ApiException(e.getMessage());
        }

        SearchResponse searchResponse = dataAccessController.search(searchStrategy,
                new Search(query, 1, limit, true)
        );

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(searchResponse)).build();
    }

    /**
     * Initiates the processing of the search
     *
     * @param query - Query to process
     * @param page  - Tha page to return
     * @return Formatted response containing the serialized report as json
     * @throws ApiException
     */
    protected javax.ws.rs.core.Response processQueryInit(Query query, int page, boolean advancedSearch) throws ApiException, ObjectSerializationException {
        if (query.getFilters() == null || query.getFilters().isEmpty() || query.getSourceType() == null) {
            throw new ApiException("Wrong search format.");
        }

        int limit = 20;
        SearchResponse searchResponse = dataAccessController.search(searchStrategy,
                new Search(query, page, limit, advancedSearch));

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(searchResponse)).build();
    }

    /**
     * Creates a <code>Query</code> instance by deserializing the JSON string
     *
     * @param queryJson- JSON to deserialize into <code>Query</code> instance
     * @return Deserialized <codeQuery</code> instance
     * @throws IOException
     * @throws QueryOptionsValidationException
     */
    private Query deserializeQuery(String queryJson) throws IOException, QueryOptionsValidationException {
        Query query = new ObjectMapper().readValue(queryJson, Query.class);
        query.validate();

        return query;
    }

    protected void isPageValid(int page) throws ApiException {
        if (page <= 0) {
            throw new ApiException("Page cannot be <= 0");
        }
        if (page > 100) {
            throw new ApiException("Page cannot be > 100");
        }
    }
}
