package kiv.zcu.knowledgeipr.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kiv.zcu.knowledgeipr.core.report.DataReport;

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

    private String summary;

    @JsonProperty("report")
    private DataReport report;

    public StandardResponse(StatusResponse status, String msg, DataReport report) {
        this.msg = msg;
        this.status = status;
        this.report = report;
    }

    public String getMsg() {
        return msg;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public DataReport getReport() {
        return report;
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

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
