package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.mongo.DbRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * TODO: Make abstract, use implementations for Mongo, ...
 *
 * Wrapper for creating reports
 */
public class ReportCreator {

    /**
     * Creates a report from the provided record list.
     * @param recordList
     * @return - The generated report
     */
    public Report createReport(List<DbRecord> recordList) {

        return new Report(recordList);
    }

    public <X, Y> GraphReport<X, Y> createChartReport(String title, String xLabel, String yLabel, List<Pair<X, Y>> data) {
        GraphReport<X, Y> report = new GraphReport<>(title, xLabel, yLabel, data);
        return report;
    }

    //TODO: refactor
    public JsonNode loadReportToJson(String filename) {
        try {
            Properties properties = AppServletContextListener.getProperties();
            String basePath = properties.getProperty("reports");

            String content = new String(Files.readAllBytes(Paths.get(basePath + filename)));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
