package kiv.zcu.knowledgeipr.api.response;

public class BaseResponse {

    private StatusResponse statusResponse;

    private String message;

    public BaseResponse(StatusResponse statusResponse, String msg) {
        this.statusResponse = statusResponse;
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
