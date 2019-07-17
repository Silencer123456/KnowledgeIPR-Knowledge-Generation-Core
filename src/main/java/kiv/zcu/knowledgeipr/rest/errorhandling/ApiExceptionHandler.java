package kiv.zcu.knowledgeipr.rest.errorhandling;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiExceptionHandler implements ExceptionMapper<ApiException> {
    @Override
    public Response toResponse(ApiException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(new Gson().toJson(e.getErrorResponse())).build();
    }
}
