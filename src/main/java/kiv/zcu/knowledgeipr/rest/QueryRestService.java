package kiv.zcu.knowledgeipr.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.DataSourceType;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.report.ReportCreator;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.exception.ApiException;
import kiv.zcu.knowledgeipr.rest.exception.QueryOptionsValidationException;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;
import kiv.zcu.knowledgeipr.rest.response.ChartResponse;
import kiv.zcu.knowledgeipr.rest.response.Response;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;

import javax.ws.rs.*;
import java.io.IOException;

/**
 * Service for handling incoming REST requests
 */
@Path("/")
public class QueryRestService {

    private ReportController reportGenerator = new ReportController(new ReportCreator());

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

    @POST
    @Path("/query")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response query(Query query) throws ApiException {
        return processQueryInit(query, 1); // Use default 1
    }

    @GET
    @Path("/activeAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPatents() throws ApiException, ResponseSerializationException {
        ChartResponse response = reportGenerator.getActiveAuthors(DataSourceType.PATENT.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ApiException, ResponseSerializationException {
        ChartResponse response = reportGenerator.getActiveOwners(DataSourceType.PATENT.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ApiException, ResponseSerializationException {
        ChartResponse response = reportGenerator.getActiveAuthors(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByFosPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ApiException, ResponseSerializationException {
        ChartResponse response = reportGenerator.getCountByFos(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ApiException, ResponseSerializationException {
        ChartResponse response = reportGenerator.getCountByYear(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
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

    /**
     * Initiates the processing of the query
     *
     * @param query - Query to process
     * @param page  - Tha page to return
     * @return Formatted response containing the serialized report as json
     * @throws ApiException
     */
    private javax.ws.rs.core.Response processQueryInit(Query query, int page) throws ApiException {
        if (query.getFilters() == null || query.getFilters().isEmpty() || query.getSourceType() == null) {
            throw new ApiException(new Response(StatusResponse.ERROR, "Wrong query format."));
        }

        int limit = 20;
        StandardResponse standardResponse = reportGenerator.processQuery(query, page, limit);

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
}
