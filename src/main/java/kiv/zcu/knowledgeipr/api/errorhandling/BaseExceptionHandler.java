package kiv.zcu.knowledgeipr.api.errorhandling;

import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.logging.Logger;

abstract class BaseExceptionHandler<T extends Throwable> implements ExceptionMapper<T> {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    Response getErrorResponse(Response.Status status, int errorCode, String message) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setStatus(status.getStatusCode());
        errorMessage.setStatusName(status.getReasonPhrase());
        errorMessage.setCode(errorCode);
        errorMessage.setMessage(message);
        //StringWriter errorStackTrace = new StringWriter();
        //ex.printStackTrace(new PrintWriter(errorStackTrace));
        String content;
        try {
            content = SerializationUtils.serializeObject(errorMessage);
        } catch (ObjectSerializationException e) {
            content = e.getMessage();
        }

        return Response.status(errorMessage.getStatus())
                .entity(content)
                .type(MediaType.APPLICATION_JSON)
                .build();

    }
}
