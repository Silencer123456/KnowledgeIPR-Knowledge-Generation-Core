package kiv.zcu.knowledgeipr.core.model.report;

import com.fasterxml.jackson.databind.JsonNode;

public interface IReport extends ISavable {

    JsonNode asJson();
}
