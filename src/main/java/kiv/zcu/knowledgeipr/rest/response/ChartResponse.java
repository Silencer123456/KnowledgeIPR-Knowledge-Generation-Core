package kiv.zcu.knowledgeipr.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import kiv.zcu.knowledgeipr.rest.StatusResponse;

//TODO: Refactor: make abstract class for reports
public class ChartResponse {
    /**
     * Status of the response. OK or ERROR
     */
    private String msg;
    private StatusResponse status;

    /**
     * Json representation of a report to be included in the response
     */
    @JsonProperty("report")
    private JsonNode reportJson;

    public ChartResponse(StatusResponse status, String msg, JsonNode reportJson) {
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

    public JsonNode getReportJson() {
        return reportJson;
    }
}
