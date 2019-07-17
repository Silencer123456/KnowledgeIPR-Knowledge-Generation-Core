package kiv.zcu.knowledgeipr.rest.errorhandling;

import com.google.gson.Gson;
import com.mongodb.MongoCommandException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// TODO replace
@Provider
public class MongoExceptionHandler implements ExceptionMapper<MongoCommandException> {
    @Override
    public Response toResponse(MongoCommandException e) {
        String response = e.getMessage();
        return Response.serverError().entity(new Gson().toJson(response)).build();
    }
}
