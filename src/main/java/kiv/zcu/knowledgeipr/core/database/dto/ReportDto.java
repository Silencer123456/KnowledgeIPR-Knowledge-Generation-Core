package kiv.zcu.knowledgeipr.core.database.dto;

import java.time.LocalDate;

public class ReportDto {
    private int id;
    private QueryDto query;
    private int docsPerPage;
    private String reportText;
    private LocalDate dateGenerated;
    private LocalDate dateUpdated;
    private int page;

    public ReportDto(QueryDto query, int docsPerPage, String reportText, LocalDate dateGenerated, LocalDate dateUpdated, int page) {
        this.query = query;
        this.docsPerPage = docsPerPage;
        this.reportText = reportText;
        this.dateGenerated = dateGenerated;
        this.dateUpdated = dateUpdated;
        this.page = page;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QueryDto getQuery() {
        return query;
    }

    public void setQuery(QueryDto query) {
        this.query = query;
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

    public LocalDate getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(LocalDate dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public LocalDate getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDate dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
