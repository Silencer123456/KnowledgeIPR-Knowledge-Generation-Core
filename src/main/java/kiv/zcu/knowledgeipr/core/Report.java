package kiv.zcu.knowledgeipr.core;

import com.google.gson.*;

import java.util.List;

/**
 * Contains the returned data ready to be sent to the client.
 * Plus contains summary.....
 */
public class Report {
    private String summary = "Test summary";
    private List<DbRecord> records;

    public Report(List<DbRecord> records) {
        this.records = records;
    }

    public List<DbRecord> getRecords() {
        return records;
    }

    public String getSummary() {
        return summary;
    }

    public JsonElement getAsJson() {
        Gson gson = new Gson();
        JsonObject dataRoot = new JsonObject();

        JsonArray array = new JsonArray();
        for (DbRecord record : records) {
            //JsonElement elem = gson.toJsonTree(record.getDocument().toJson());
            JsonObject o = new JsonParser().parse(record.getDocument().toJson()).getAsJsonObject();
            array.add(o);
        }

        dataRoot.addProperty("summary", summary);
        dataRoot.add("documents", array);

        return dataRoot;
    }
}
