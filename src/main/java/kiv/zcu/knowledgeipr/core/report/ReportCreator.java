package kiv.zcu.knowledgeipr.core.report;

import kiv.zcu.knowledgeipr.core.DbRecord;

import java.util.List;

/**
 * TODO: Make abstract, use implementations for Mongo, ...
 *
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
}