package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Report holding data for statistical queries
 * TODO: implement generic interface with method getAsJson...
 */
public class GraphReport<X, Y> {

    private String title;
    private String xLabel;
    private String yLabel;
    private List<Pair<X, Y>> data;

    public GraphReport(String title, String xLabel, String yLabel, List<Pair<X, Y>> data) {
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.data = data;
    }

    /**
     * Returns a JSON representation of the object
     *
     * @return - Json element of the object
     */
    public JsonNode getAsJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode titleNode = mapper.createObjectNode();
        titleNode.put("text", title);
        rootNode.set("title", titleNode);

        titleNode = mapper.createObjectNode();
        titleNode.put("text", xLabel);
        rootNode.set("xAxis", titleNode);

        titleNode = mapper.createObjectNode();
        titleNode.put("text", yLabel);
        rootNode.set("yAxis", titleNode);

        ArrayNode dataNode = mapper.createArrayNode();
        for (Pair<X, Y> dataPair : data) {
            ObjectNode pairNode = mapper.createObjectNode();
            // TODO: check the type of the pairs!!!
            pairNode.put("x", (String) dataPair.getKey());
            pairNode.put("y", (Integer) dataPair.getValue());
            dataNode.add(pairNode);
        }

        rootNode.putArray("data").addAll(dataNode);

        return rootNode;
    }

    /**
     * Saves the json to the filesystem, so it can be retrieved later
     */
    public boolean save() {
        try {
            String json = SerializationUtils.serializeObject(this);
            // TODO: Read path from property file
            Files.write(Paths.get("C:\\Users\\UWB-Dalibor\\Desktop\\tmp\\test.json"), json.getBytes());
            return true;
        } catch (IOException | ResponseSerializationException e) {
            e.printStackTrace();
        }

        return false;
    }
}
