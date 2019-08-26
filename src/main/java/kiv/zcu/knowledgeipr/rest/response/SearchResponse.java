package kiv.zcu.knowledgeipr.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kiv.zcu.knowledgeipr.core.report.DataReport;

/**
 * Holds the data for the response to the user's search request.
 */
public class SearchResponse {
    /**
     * Description text of the response
     */
    private String msg;

    /**
     * Status of the response. OK or ERROR
     */
    private StatusResponse status;

    /**
     * Number of searched documents
     */
    private int searchedCount;

    /**
     * Number of returned results
     */
    private int count;

    /**
     * Page number of the results
     */
    private int page;

    @JsonProperty("report")
    private DataReport report;

    public SearchResponse(StatusResponse status, String msg, DataReport report) {
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

    public void setSearchedCount(int searchedCount) {
        this.searchedCount = searchedCount;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
