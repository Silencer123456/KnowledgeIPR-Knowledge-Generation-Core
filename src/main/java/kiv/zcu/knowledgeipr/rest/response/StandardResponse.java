package kiv.zcu.knowledgeipr.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonElement;

/**
 * Holds the data for the response
 */
public class StandardResponse {
    /**
     * Status of the response. OK or ERROR
     */
    private String msg;
    private StatusResponse status;
    private int requestTime;
    private int searchedCount;
    private int returnedCount;
    private int page;

    @JsonProperty("report")
    private JsonElement reportJson;

    public StandardResponse(StatusResponse status, String msg, JsonElement reportJson) {
        this.msg = msg;
        this.status = status;
        this.reportJson = reportJson;
    }

    public String getMsg() {
        return msg;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public JsonElement getReportJson() {
        return reportJson;
    }

    public void setRequestTime(int requestTime) {
        this.requestTime = requestTime;
    }

    public void setSearchedCount(int searchedCount) {
        this.searchedCount = searchedCount;
    }

    public void setReturnedCount(int returnedCount) {
        this.returnedCount = returnedCount;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
