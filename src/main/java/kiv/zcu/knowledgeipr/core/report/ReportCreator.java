package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.dataaccess.DbRecord;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;

import java.util.List;
import java.util.Properties;

/**
 * Wrapper enabling the instantiation of various reports and contains methods for manipulating them
 */
public class ReportCreator {
    private IReportRepository reportRepository;

    public ReportCreator(IReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Creates a report from the provided record list.
     * @param recordList - List of records
     * @return - The generated report
     */
    public DataReport createReport(List<DbRecord> recordList) {

        return new DataReport(recordList);
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
