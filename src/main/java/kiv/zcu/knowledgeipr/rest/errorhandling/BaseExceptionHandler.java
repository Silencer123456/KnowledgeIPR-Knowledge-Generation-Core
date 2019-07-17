package kiv.zcu.knowledgeipr.rest.errorhandling;

import com.google.gson.Gson;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

abstract class BaseExceptionHandler<T extends Throwable> implements ExceptionMapper<T> {

    Response getErrorResponse(Response.Status status, int errorCode, String message) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setStatus(status.getStatusCode());
        errorMessage.setStatusName(status.getReasonPhrase());
        errorMessage.setCode(errorCode);
        errorMessage.setMessage(message);
        //StringWriter errorStackTrace = new StringWriter();
        //ex.printStackTrace(new PrintWriter(errorStackTrace));

        return Response.status(errorMessage.getStatus())
                .entity(new Gson().toJson(errorMessage))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
