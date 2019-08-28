package kiv.zcu.knowledgeipr.api.errorhandling;

public class ErrorMessage {

    /**
     * contains the same HTTP Status code returned by the server
     */
    int status;

    String statusName;

    /**
     * application specific error code
     */
    int code;

    /**
     * message describing the error
     */
    String message;

    /**
     * link point to page where the error message is documented
     */
    String link;

    /**
     * extra information that might useful for developers
     */
    String developerMessage;

    public ErrorMessage() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}