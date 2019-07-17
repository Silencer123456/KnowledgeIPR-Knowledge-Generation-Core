package kiv.zcu.knowledgeipr.rest.errorhandling;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ResponseSerializationExceptionHandler implements ExceptionMapper<ObjectSerializationException> {
    @Override
    public Response toResponse(ObjectSerializationException e) {
        return Response.status(Response.Status.NO_CONTENT).entity(new Gson().toJson(e.getErrorResponse())).build();
    }
}
