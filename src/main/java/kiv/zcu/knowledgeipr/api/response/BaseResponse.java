package kiv.zcu.knowledgeipr.api.response;

public class BaseResponse {

    private ResponseStatus responseStatus;

    private String message;

    public BaseResponse(ResponseStatus responseStatus, String msg) {
        this.responseStatus = responseStatus;
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
