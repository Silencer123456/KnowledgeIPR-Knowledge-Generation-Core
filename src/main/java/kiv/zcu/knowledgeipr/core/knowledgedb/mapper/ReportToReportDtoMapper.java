package kiv.zcu.knowledgeipr.core.knowledgedb.mapper;

import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

public class ReportToReportDtoMapper implements Mapper<SearchReport, ReportDto> {

    private int page;
    private int limit;
    private long queryId;
    private String dbEngine;
    private String sourceType;

    public ReportToReportDtoMapper(int page, int limit, long queryId, String dbEngine, String sourceType) {
        this.page = page;
        this.limit = limit;
        this.queryId = queryId;
        this.dbEngine = dbEngine;
        this.sourceType = sourceType;
    }

    @Override
    public ReportDto map(SearchReport searchReport) throws ObjectSerializationException {
        return new ReportDto(queryId, limit, SerializationUtils.serializeObject(searchReport), null, null, page, dbEngine, sourceType);
    }
}
