package kiv.zcu.knowledgeipr.rest.response;

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
