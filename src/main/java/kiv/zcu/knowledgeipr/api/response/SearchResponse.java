package kiv.zcu.knowledgeipr.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;

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
    private SearchReport report;

    public SearchResponse(StatusResponse status, String msg, SearchReport report) {
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

    public SearchReport getReport() {
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
