package kiv.zcu.knowledgeipr.rest.exception;

import kiv.zcu.knowledgeipr.rest.response.Response;

import java.io.Serializable;

public class ResponseSerializationException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    private Response errorResponse;

    public ResponseSerializationException(Response errorResponse) {
        super(errorResponse.getMessage());

        this.errorResponse = errorResponse;
    }

    public ResponseSerializationException(Response errorResponse, Exception e) {
        super(errorResponse.getMessage(), e);

        this.errorResponse = errorResponse;

    }

    public Response getErrorResponse() {
        return errorResponse;
    }
}
