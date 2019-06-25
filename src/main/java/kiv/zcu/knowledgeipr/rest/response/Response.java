package kiv.zcu.knowledgeipr.rest.response;

import kiv.zcu.knowledgeipr.rest.StatusResponse;

public class Response {

    private StatusResponse statusResponse;

    private String message;

    public Response(StatusResponse statusResponse, String msg) {
        this.statusResponse = statusResponse;
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
