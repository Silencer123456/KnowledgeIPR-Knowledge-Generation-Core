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
}
