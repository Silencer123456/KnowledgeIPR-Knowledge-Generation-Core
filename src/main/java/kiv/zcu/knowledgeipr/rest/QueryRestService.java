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
    public Response hello(@PathParam("page") int page, Query query) throws ApiException {

        if (page == 0) {
            throw new ApiException("ID cannot be 0");
        }
        //Query query = new Gson().fromJson(request.body(), Query.class);
        StandardResponse standardResponse = reportGenerator.processQuery(query, page);

        return Response.ok().entity(new Gson().toJson(standardResponse)).build();
        //return Response.status(200).entity("GOOOOD").build();
    }
}
