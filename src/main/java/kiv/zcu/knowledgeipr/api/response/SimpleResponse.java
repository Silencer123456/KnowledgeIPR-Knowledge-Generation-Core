package kiv.zcu.knowledgeipr.api.response;

import com.fasterxml.jackson.databind.JsonNode;

public class SimpleResponse {

    private JsonNode content;

    public SimpleResponse(JsonNode content) {
        this.content = content;
    }
}
