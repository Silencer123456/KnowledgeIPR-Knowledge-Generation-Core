package kiv.zcu.knowledgeipr.rest;

import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.Query;
import kiv.zcu.knowledgeipr.core.ReportCreator;
import kiv.zcu.knowledgeipr.core.ReportManager;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;
import kiv.zcu.knowledgeipr.rest.exception.ApiException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/")
public class QueryRestService {

    private ReportManager reportGenerator = new ReportManager(new ReportCreator()); // TODO: change to concrete impl...

    @POST
    @Path("/query/{page}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response query(@PathParam("page") int page, Query query) throws ApiException {
        if (page == 0) {
            throw new ApiException("ID cannot be 0");
        }

        int limit = 30;

        //Query query = new Gson().fromJson(request.body(), Query.class);
        StandardResponse standardResponse = reportGenerator.processQuery(query, page, limit);

        return Response.ok().entity(new Gson().toJson(standardResponse)).build();
        //return Response.status(200).entity("GOOOOD").build();
    }

    @POST
    @Path("/queryLimit/{limit}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response queryWithLimit(@PathParam("limit") int limit, Query query) throws ApiException {
        if (limit > 1000) {
            throw new ApiException("Limit cannot exceed 1000");
        }
        //Query query = new Gson().fromJson(request.body(), Query.class);
        StandardResponse standardResponse = reportGenerator.processQuery(query, 1, limit);

        return Response.ok().entity(new Gson().toJson(standardResponse)).build();
        //return Response.status(200).entity("GOOOOD").build();
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.ok().entity("OKOKOKOKOK").build();
    }
}
