package kiv.zcu.knowledgeipr.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.DataSourceType;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.report.ReportCreator;
import kiv.zcu.knowledgeipr.core.report.ReportFilename;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.exception.ApiException;
import kiv.zcu.knowledgeipr.rest.exception.QueryOptionsValidationException;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;
import kiv.zcu.knowledgeipr.rest.response.*;

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
     * @param page      - page of results to return
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
    public javax.ws.rs.core.Response getActiveAuthorsPatents() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_OWNERS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_FOS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_PUBLISHER);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_VENUES);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_KEYWORD);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_YEAR);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPatents() throws ResponseSerializationException {
        SimpleResponse response = reportGenerator.getCountAuthors(DataSourceType.PATENT.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPublications() throws ResponseSerializationException {
        SimpleResponse response = reportGenerator.getCountAuthors(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Path("/generateStats")
    public javax.ws.rs.core.Response generateStats() {
        reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_AUTHORS);
        reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_OWNERS);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.ACTIVE_AUTHORS);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_FOS);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_YEAR);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_PUBLISHER);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_KEYWORD);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_VENUES);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_LANG);
        //reportGenerator.getCountAuthors(DataSourceType.PATENT.value);
        //reportGenerator.getCountAuthors(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().build();
    }

    @GET
    @Path("/synonyms/{word}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getSynonymsForWord(@PathParam("word") String word) throws ResponseSerializationException {
        WordNetResponse response = reportGenerator.getSynonyms(word);

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
