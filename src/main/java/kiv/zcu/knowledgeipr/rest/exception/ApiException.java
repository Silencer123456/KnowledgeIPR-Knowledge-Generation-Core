package kiv.zcu.knowledgeipr.rest.exception;

import kiv.zcu.knowledgeipr.rest.response.BaseResponse;

import java.io.Serializable;

public class ApiException extends Exception implements Serializable
{
    private static final long serialVersionUID = 1L;

    private BaseResponse errorResponse;

    public ApiException(BaseResponse errorResponse) {
        super(errorResponse.getMessage());

        this.errorResponse = errorResponse;
    }

    public ApiException(BaseResponse errorResponse, Exception e) {
        super(errorResponse.getMessage(), e);

        this.errorResponse = errorResponse;

    }

    public BaseResponse getErrorResponse() {
        return errorResponse;
    }
}