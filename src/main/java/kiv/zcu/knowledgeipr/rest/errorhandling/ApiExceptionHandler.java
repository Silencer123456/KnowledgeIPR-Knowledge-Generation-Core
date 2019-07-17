package kiv.zcu.knowledgeipr.rest.errorhandling;

import kiv.zcu.knowledgeipr.core.utils.AppConstants;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiExceptionHandler extends BaseExceptionHandler<ApiException> {
    @Override
    public Response toResponse(ApiException ex) {
        return getErrorResponse(Response.Status.BAD_REQUEST,
                AppConstants.API_EXCEPTION_ERROR_CODE,
                ex.getMessage());
    }
}