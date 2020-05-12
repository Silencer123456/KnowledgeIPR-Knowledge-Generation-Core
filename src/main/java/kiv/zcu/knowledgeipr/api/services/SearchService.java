package kiv.zcu.knowledgeipr.api.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.api.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryOptionsValidationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.api.response.BaseResponse;
import kiv.zcu.knowledgeipr.api.response.ResponseStatus;
import kiv.zcu.knowledgeipr.api.response.SearchResponse;
import kiv.zcu.knowledgeipr.api.response.WordNetResponse;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification.SimpleTextSearchElasticSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.websearch.WebSearcher;
import kiv.zcu.knowledgeipr.utils.AppConstants;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import javax.ws.rs.*;
import java.io.IOException;

/**
 * Service for handling incoming REST requests
 */
public abstract class SearchService<T extends IDataSearcher> {

    private DataAccessController dataAccessController;

    protected SearchStrategy<Search, T> searchStrategy;

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
    public javax.ws.rs.core.Response search(@QueryParam("sourceType") String sourceType, @QueryParam("page") int page, String queryJson) throws ApiException, ObjectSerializationException {
        DataSourceType dataSourceType = DataSourceType.getByValue(sourceType);
        if (dataSourceType == null) {
            dataSourceType = DataSourceType.ALL; // Wont work with MongoDB
        }

        isPageValid(page);

        Query query;
        try {
            query = deserializeQuery(queryJson);

        } catch (IOException | QueryOptionsValidationException e) {
            e.printStackTrace();
            throw new ApiException(e.getMessage());
        }

        Search search = new Search(query, dataSourceType, page, AppConstants.RESULTS_LIMIT, true, searchStrategy.getSearchEngineName());
        SearchSpecification<Search> searchSpecification = new SimpleTextSearchElasticSpecification<>(search);

        return initSearch(searchSpecification);
    }

    @GET
    @Logged
    @Path("/web/")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response searcWeb(@QueryParam("query") String query) {
        JsonNode s = WebSearcher.getWebSearchResults(query);
        String json;
        if (s != null) {
            json = s.toString();
        } else {
            json = "";
        }
        return javax.ws.rs.core.Response.ok().entity(json).build();
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

    /*@GET
    @Logged
    @Path("/listAllAuthors")
    @Consumes("application/json")
    @Produces("application/json")
    public abstract javax.ws.rs.core.Response listAllAuthors(@QueryParam("limit") int limit)
            throws ApiException, ObjectSerializationException;*/

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

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(new BaseResponse(ResponseStatus.SUCCESS, "OK"))).build();
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

        Search search = new Search(query, DataSourceType.PATENT, 1, limit, true, searchStrategy.getSearchEngineName());
        SearchSpecification<Search> searchSpecification = new SimpleTextSearchElasticSpecification<>(search);// TODO: Mongo does not make use of the search specifications
        SearchResponse searchResponse = dataAccessController.search(searchStrategy, searchSpecification);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(searchResponse)).build();
    }

    /**
     * Initiates the search process. Invokes the main controller and provides it with the searching
     * strategy and the search specification.
     *
     * @param searchSpecification - The search specification
     * @return Formatted response containing the serialized report as json
     * @throws ApiException if the query is malformed
     */
    protected javax.ws.rs.core.Response initSearch(SearchSpecification<Search> searchSpecification) throws ApiException, ObjectSerializationException {
        Search search = searchSpecification.getSearch();
        Query query = search.getQuery();
        if (query.getFilters() == null || query.getFilters().isEmpty() || search.getDataSourceType() == null) {
            throw new ApiException("Wrong query format.");
        }

        SearchResponse searchResponse = dataAccessController.search(searchStrategy, searchSpecification);

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
