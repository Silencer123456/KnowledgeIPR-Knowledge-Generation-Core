package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.utils.Constants;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * DataReport holding chart data
 * TODO: implement generic interface for all reports
 */
public class GraphReport<X, Y> implements IReport {

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
     * Warning: If the name of the method starts with get, the Jackson serializer will automatically
     * call this method when attempting to serialize the object.
     *
     * @return - Json element of the object
     */
    @Override
    public JsonNode asJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
//        ObjectNode titleNode = mapper.createObjectNode();
//        titleNode.put("text", title);
//        rootNode.set("title", titleNode);
//
//        titleNode = mapper.createObjectNode();
//        titleNode.put("text", xLabel);
//        rootNode.set("xAxis", titleNode);
//
//        titleNode = mapper.createObjectNode();
//        titleNode.put("text", yLabel);
//        rootNode.set("yAxis", titleNode);
//
//        ArrayNode dataNode = mapper.createArrayNode();
//        for (Pair<X, Y> dataPair : data) {
//            ObjectNode pairNode = mapper.createObjectNode();
//            if (dataPair.getKey() instanceof Integer) {
//                pairNode.put("x", (Integer) dataPair.getKey());
//            } else {
//                pairNode.put("x", (String) dataPair.getKey());
//            }
//            pairNode.put("y", (Integer) dataPair.getValue());
//            dataNode.add(pairNode);
//        }
//
//        rootNode.putArray("data").addAll(dataNode);

        return rootNode;
    }

    /**
     * Saves the json to the filesystem, so it can be retrieved later
     */
    @Override
    public boolean save(String filename) {
        try {
            String json = SerializationUtils.serializeObject(this);

            Properties properties = AppServletContextListener.getProperties();
            String basePath = properties.getProperty(Constants.REPORTS_RESOURCE_NAME);

            new File(basePath + filename).getParentFile().mkdirs();

            Files.write(Paths.get(basePath + filename), json.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException | ResponseSerializationException e) {
            e.printStackTrace();
        }

        return false;
    }
}
