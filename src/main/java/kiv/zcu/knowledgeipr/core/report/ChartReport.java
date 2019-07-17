package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kiv.zcu.knowledgeipr.core.dbaccess.IReportRepository;

/**
 * DataReport holding chart data
 * TODO: implement generic interface for all reports
 */
public class ChartReport<X, Y> implements IReport {

    private Chart chart;
    @JsonIgnore
    private IReportRepository repository;

    public ChartReport(Chart chart, IReportRepository repository) {
        this.chart = chart;
        this.repository = repository;
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
        return repository.save(this, filename);
    }
}
