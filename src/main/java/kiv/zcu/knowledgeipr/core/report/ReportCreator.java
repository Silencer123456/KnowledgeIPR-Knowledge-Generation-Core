package kiv.zcu.knowledgeipr.core.report;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.mongo.DbRecord;

import java.util.List;

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
}
