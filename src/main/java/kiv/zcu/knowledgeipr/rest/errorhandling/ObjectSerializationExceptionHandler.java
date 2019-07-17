package kiv.zcu.knowledgeipr.rest.errorhandling;

import kiv.zcu.knowledgeipr.core.utils.AppConstants;

import javax.ws.rs.core.Response;

public class ObjectSerializationExceptionHandler extends BaseExceptionHandler<ObjectSerializationException> {
    @Override
    public Response toResponse(ObjectSerializationException ex) {
        return getErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                AppConstants.API_EXCEPTION_ERROR_CODE,
                ex.getMessage());
    }
}
