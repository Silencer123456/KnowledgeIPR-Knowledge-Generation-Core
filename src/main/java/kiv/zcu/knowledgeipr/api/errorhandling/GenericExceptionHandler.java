package kiv.zcu.knowledgeipr.api.errorhandling;

import kiv.zcu.knowledgeipr.utils.AppConstants;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

public class GenericExceptionHandler extends BaseExceptionHandler<Throwable> {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public Response toResponse(Throwable ex) {
        LOGGER.warning(ExceptionUtils.getStackTrace(ex));

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
