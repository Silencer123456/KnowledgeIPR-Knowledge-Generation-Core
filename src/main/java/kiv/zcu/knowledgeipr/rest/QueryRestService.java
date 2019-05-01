package kiv.zcu.knowledgeipr.rest;

import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.Query;
import kiv.zcu.knowledgeipr.core.ReportCreator;
import kiv.zcu.knowledgeipr.core.ReportManager;
import kiv.zcu.knowledgeipr.rest.exception.ApiException;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

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
     * @param query - request query
     * @return
     * @throws ApiException
     */
    @POST
    @Path("/query/{page}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response query(@PathParam("page") int page, Query query) throws ApiException {
        if (page == 0) {
            throw new ApiException("ID cannot be 0");
        }

        int limit = 30;

        StandardResponse standardResponse = reportGenerator.processQuery(query, page, limit);

        return Response.ok().entity(new Gson().toJson(standardResponse)).build();
    }

    @POST
    @Path("/queryLimit/{limit}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response queryWithLimit(@PathParam("limit") int limit, Query query) throws ApiException {
        if (limit > 1000) {
            throw new ApiException("Limit cannot exceed 1000");
        }
        StandardResponse standardResponse = reportGenerator.processQuery(query, 1, limit);

        return Response.ok().entity(new Gson().toJson(standardResponse)).build();
    }
}
