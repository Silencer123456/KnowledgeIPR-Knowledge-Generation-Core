package kiv.zcu.knowledgeipr.core.model.report;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import java.util.List;
import java.util.Properties;

/**
 * Wrapper enabling the instantiation of various reports and contains methods for manipulating them
 */
public class ReportHandler {
    private IReportSavable reportRepository;

    public ReportHandler(IReportSavable reportRepository) {
        this.reportRepository = reportRepository;
    }

    public <X, Y> ChartReport<X, Y> createChartReport(String title, String xLabel, String yLabel, List<Pair<X, Y>> data) {
        ChartReport<X, Y> report = new ChartReport<>(new Chart<>(title, xLabel, yLabel, data), reportRepository);
        return report;
    }

    public JsonNode loadReportToJsonFromFile(String filename) {
        Properties properties = AppServletContextListener.getProperties();
        String basePath = properties.getProperty("reports");

        if (basePath == null) return null;

        return SerializationUtils.objectToJsonFromFile(basePath + filename);
    }
}
