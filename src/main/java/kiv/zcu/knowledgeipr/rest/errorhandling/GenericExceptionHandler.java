package kiv.zcu.knowledgeipr.rest.errorhandling;

import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.utils.AppConstants;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.io.PrintWriter;
import java.io.StringWriter;

public class GenericExceptionHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        ErrorMessage errorMessage = new ErrorMessage();
        setHttpStatus(ex, errorMessage);
        errorMessage.setCode(AppConstants.GENERIC_APP_ERROR_CODE);
        errorMessage.setMessage(ex.getMessage());
        StringWriter errorStackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(errorStackTrace));
        //errorMessage.setDeveloperMessage(errorStackTrace.toString());

        return Response.status(errorMessage.getStatus())
                .entity(new Gson().toJson(errorMessage))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void setHttpStatus(Throwable ex, ErrorMessage errorMessage) {
        if (ex instanceof WebApplicationException) {
            errorMessage.setStatus(((WebApplicationException) ex).getResponse().getStatus());
        } else {
            errorMessage.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()); //defaults to internal server error 500
        }
    }
}
