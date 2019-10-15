package kiv.zcu.knowledgeipr.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;

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
    private ResponseStatus status;

    /**
     * Searched collection
     */
    private DataSourceType searchedDataType;

    /**
     * Number of documents in the searched collection
     */
    private int docsInCollection;

    /**
     * Number of returned results
     */
    private int docsReturned;

    /**
     * Page number of the results
     */
    private int page;

    @JsonProperty("report")
    private SearchReport report;

    public SearchResponse(ResponseStatus status, String msg, SearchReport report) {
        this.msg = msg;
        this.status = status;
        this.report = report;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public SearchReport getReport() {
        return report;
    }

    public void setDocsInCollection(int docsInCollection) {
        this.docsInCollection = docsInCollection;
    }

    public void setSearchedType(DataSourceType searchedCollection) {
        this.searchedDataType = searchedCollection;
    }

    public void setDocsReturned(int docsReturned) {
        this.docsReturned = docsReturned;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
