package kiv.zcu.knowledgeipr.core.knowledgedb.dto;

import java.sql.Timestamp;

/**
 * Report Data Transfer Object, representing a table
 * in the SQL database.
 */
public class ReportDto {
    private long reportId;
    private long queryId;
    private int docsPerPage;
    private String reportText;
    private Timestamp dateGenerated;
    private Timestamp dateUpdated;
    private int page;
    private String dbEngine;
    private String sourceType;

    /**
     * Needs empty constructor for the serialization from the database
     */
    public ReportDto() {
    }

    public ReportDto(long queryId, int docsPerPage, String reportText, Timestamp dateGenerated, Timestamp dateUpdated, int page, String dbEngine, String sourceType) {
        this.queryId = queryId;
        this.docsPerPage = docsPerPage;
        this.reportText = reportText;
        this.dateGenerated = dateGenerated;
        this.dateUpdated = dateUpdated;
        this.page = page;
        this.dbEngine = dbEngine;
        this.sourceType = sourceType;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }

    public long getQueryId() {
        return queryId;
    }

    public void setQueryId(long query) {
        this.queryId = query;
    }

    public int getDocsPerPage() {
        return docsPerPage;
    }

    public void setDocsPerPage(int docsPerPage) {
        this.docsPerPage = docsPerPage;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public Timestamp getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(Timestamp dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public Timestamp getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Timestamp dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getDbEngine() {
        return dbEngine;
    }

    public void setDbEngine(String dbEngine) {
        this.dbEngine = dbEngine;


    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
