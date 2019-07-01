package kiv.zcu.knowledgeipr.rest;

import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.Query;
import kiv.zcu.knowledgeipr.core.ReportCreator;
import kiv.zcu.knowledgeipr.core.ReportManager;
import kiv.zcu.knowledgeipr.rest.exception.ApiException;
import kiv.zcu.knowledgeipr.rest.exception.QueryOptionsValidationException;
import kiv.zcu.knowledgeipr.rest.response.Response;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.*;
import java.io.IOException;

/**
 * Service for handling incoming REST requests
 */
@Path("/")
public class QueryRestService {

    private ReportManager reportGenerator = new ReportManager(new ReportCreator());

    /**
     * Accepts a query, processes it and
     * returns a set of results as JSON to the caller.
     *
     * @param page  - page of results to return
     * @param queryJson - request query json
     * @return JSON response
     * @throws ApiException
     */
    @POST
    @Path("/query/{page}")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response query(@PathParam("page") int page, String queryJson) throws ApiException {
        if (page <= 0) {
            throw new ApiException(new Response(StatusResponse.ERROR, "Page cannot be <= 0"));
        }
        if (page > 1000) {
            throw new ApiException(new Response(StatusResponse.ERROR, "Page cannot be > 1000"));

        }
        Query query;
        try {
            query = deserializeQuery(queryJson);

        } catch (IOException | QueryOptionsValidationException e) {
            e.printStackTrace();
            throw new ApiException(new Response(StatusResponse.ERROR, e.getMessage()));
        }

        return processQueryInit(query, page);
    }

    private Query deserializeQuery(String queryJson) throws IOException, QueryOptionsValidationException {
        Query query = new ObjectMapper().readValue(queryJson, Query.class);
        query.validate();

        return query;
    }

    @POST
    @Path("/query")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response query(Query query) throws ApiException {
        return processQueryInit(query, 1); // Use default 1
    }

    @POST
    @Path("/queryLimit/{limit}")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response queryWithLimit(@PathParam("limit") int limit, String queryJson) throws ApiException {
        if (limit > 1000) {
            throw new ApiException(new Response(StatusResponse.ERROR, "Limit cannot exceed 1000"));
        }

        Query query;
        try {
            query = deserializeQuery(queryJson);

        } catch (IOException | QueryOptionsValidationException e) {
            e.printStackTrace();
            throw new ApiException(new Response(StatusResponse.ERROR, e.getMessage()));
        }

        StandardResponse standardResponse = reportGenerator.processQuery(query, 1, limit);

        return javax.ws.rs.core.Response.ok().entity(new Gson().toJson(standardResponse)).build();
    }

    private javax.ws.rs.core.Response processQueryInit(Query query, int page) throws ApiException {
        if (query.getFilters() == null || query.getFilters().isEmpty() || query.getSourceType() == null) {
            throw new ApiException(new Response(StatusResponse.ERROR, "Wrong query format."));
        }

        int limit = 30;
        StandardResponse standardResponse = reportGenerator.processQuery(query, page, limit);

        return javax.ws.rs.core.Response.ok().entity(new Gson().toJson(standardResponse)).build();
    }
}
