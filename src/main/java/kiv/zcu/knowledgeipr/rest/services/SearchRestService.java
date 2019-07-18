package kiv.zcu.knowledgeipr.rest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.errorhandling.QueryOptionsValidationException;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;
import kiv.zcu.knowledgeipr.rest.response.WordNetResponse;

import javax.ws.rs.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling incoming REST requests
 */
@Path("/search/")
public class SearchRestService {

    private ReportController reportController;

    public SearchRestService(ReportController reportController) {
        this.reportController = reportController;
    }

    /**
     * Accepts a query, processes it and returns a set of results as JSON to the caller.
     *
     * @param page      - page of results to return
     * @param queryJson - request query json
     * @return JSON response
     * @throws ApiException - In case of user api errors
     */
    @POST
    @Path("/")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response search(@QueryParam("page") int page, String queryJson) throws ApiException {
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
     * @param ownerName
     * @param year
     * @return
     * @throws ApiException
     */
    @POST
    @Path("/owners")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response ownersSearch(@QueryParam("page") int page,
                                                  @QueryParam("owner") String ownerName,
                                                  @QueryParam("year") int year) throws ApiException {
        isPageValid(page);

        Map<String, String> filters = new HashMap<>();
        filters.put(ResponseField.OWNERS_NAME.value, ownerName);
        Map<String, Map<String, Integer>> conditions = new HashMap<>();

        Map<String, Integer> yearMap = new HashMap<>();
        if (year > 0) {
            yearMap.put("$eq", year);
            conditions.put(ResponseField.YEAR.value, yearMap);
        }

        Map<String, Object> options = new HashMap<>();
        options.put("timeout", 50);

        Query query = new Query(DataSourceType.PATENT.value, filters, conditions, options);

        return processQueryInit(query, page, false);
    }

    @GET
    @Path("/synonyms/{word}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getSynonymsForWord(@PathParam("word") String word) throws ObjectSerializationException {
        WordNetResponse response = reportController.getSynonyms(word);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Path("/limited/{limit}")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response queryWithLimit(@PathParam("limit") int limit, String queryJson) throws ApiException {
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

        StandardResponse standardResponse = reportController.runSearch(query, 1, limit, true);

        return javax.ws.rs.core.Response.ok().entity(new Gson().toJson(standardResponse)).build();
    }

    /**
     * Initiates the processing of the query
     *
     * @param query - Query to process
     * @param page  - Tha page to return
     * @return Formatted response containing the serialized report as json
     * @throws ApiException
     */
    private javax.ws.rs.core.Response processQueryInit(Query query, int page, boolean advancedSearch) throws ApiException {
        if (query.getFilters() == null || query.getFilters().isEmpty() || query.getSourceType() == null) {
            throw new ApiException("Wrong query format.");
        }

        int limit = 20;
        StandardResponse standardResponse = reportController.runSearch(query, page, limit, advancedSearch);

        return javax.ws.rs.core.Response.ok().entity(new Gson().toJson(standardResponse)).build();
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

    private void isPageValid(int page) throws ApiException {
        if (page <= 0) {
            throw new ApiException("Page cannot be <= 0");
        }
        if (page > 100) {
            throw new ApiException("Page cannot be > 100");
        }
    }
}
