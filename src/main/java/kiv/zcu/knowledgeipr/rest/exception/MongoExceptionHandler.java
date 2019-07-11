package kiv.zcu.knowledgeipr.rest.exception;

import com.google.gson.Gson;
import com.mongodb.MongoCommandException;
import kiv.zcu.knowledgeipr.rest.response.BaseResponse;
import kiv.zcu.knowledgeipr.rest.response.StatusResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MongoExceptionHandler implements ExceptionMapper<MongoCommandException> {
    @Override
    public Response toResponse(MongoCommandException e) {
        BaseResponse response = new BaseResponse(StatusResponse.ERROR, e.getMessage());
        return Response.serverError().entity(new Gson().toJson(response)).build();
    }
}
