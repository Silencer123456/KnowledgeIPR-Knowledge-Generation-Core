package kiv.zcu.knowledgeipr.rest.exception;

import com.google.gson.Gson;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ResponseSerializationExceptionHandler implements ExceptionMapper<ResponseSerializationException> {
    @Override
    public Response toResponse(ResponseSerializationException e) {
        return Response.status(Response.Status.NO_CONTENT).entity(new Gson().toJson(e.getErrorResponse())).build();
    }
}
