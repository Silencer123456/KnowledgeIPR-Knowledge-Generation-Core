package kiv.zcu.knowledgeipr.rest.errorhandling;

import kiv.zcu.knowledgeipr.core.utils.AppConstants;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class GenericExceptionHandler extends BaseExceptionHandler<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        ex.printStackTrace();
        return getErrorResponse(getHttpStatus(ex),
                AppConstants.API_EXCEPTION_ERROR_CODE,
                ex.getMessage());
    }

    private Response.Status getHttpStatus(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            int status = ((WebApplicationException) ex).getResponse().getStatus();
            return Response.Status.fromStatusCode(status);
        }
        return Response.Status.INTERNAL_SERVER_ERROR; //defaults to internal server error 500
    }
}
