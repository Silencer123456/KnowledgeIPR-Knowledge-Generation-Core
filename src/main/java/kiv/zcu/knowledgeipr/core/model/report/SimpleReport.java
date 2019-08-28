package kiv.zcu.knowledgeipr.core.model.report;

import com.fasterxml.jackson.databind.JsonNode;

public class SimpleReport implements IReport {
    private int value;

    public SimpleReport(int value) {
        this.value = value;
    }

    @Override
    public boolean save(String filename) {
        return false;
    }

    @Override
    public JsonNode asJson() {
        return null;
    }
}
