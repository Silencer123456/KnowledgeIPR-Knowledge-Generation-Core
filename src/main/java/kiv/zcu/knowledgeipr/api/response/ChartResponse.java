package kiv.zcu.knowledgeipr.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

//TODO: Refactor: make abstract class for reports
public class ChartResponse {
    /**
     * Status of the response. OK or ERROR
     */
    private String msg;
    private ResponseStatus status;

    /**
     * Json representation of a report to be included in the response
     */
    @JsonProperty("report")
    private JsonNode reportJson;

    public ChartResponse(ResponseStatus status, String msg, JsonNode reportJson) {
        this.msg = msg;
        this.status = status;
        this.reportJson = reportJson;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public JsonNode getReportJson() {
        return reportJson;
    }
}
