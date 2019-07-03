package kiv.zcu.knowledgeipr.core.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kiv.zcu.knowledgeipr.core.DbRecord;

import java.util.List;

/**
 * Contains the returned data ready to be sent to the client.
 * Plus contains summary etc.
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

    /**
     * Returns a JSON representation of the object
     *
     * @return - Json element of the object
     */
    public JsonElement getAsJson() {
        JsonObject dataRoot = new JsonObject();

        JsonArray array = new JsonArray();
        for (DbRecord record : records) {
            JsonObject o = new JsonParser().parse(record.getDocument().toJson()).getAsJsonObject();
            array.add(o);
        }

        dataRoot.addProperty("summary", summary);
        dataRoot.add("documents", array);

        return dataRoot;
    }
}
