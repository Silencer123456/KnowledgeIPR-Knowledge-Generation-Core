package kiv.zcu.knowledgeipr.core.database.mapper;

import kiv.zcu.knowledgeipr.core.database.dto.ReportDto;
import kiv.zcu.knowledgeipr.core.report.SearchReport;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;

public class ReportToReportDtoMapper implements Mapper<SearchReport, ReportDto> {

    private int page;
    private int limit;
    private long queryId;

    public ReportToReportDtoMapper(int page, int limit, long queryId) {
        this.page = page;
        this.limit = limit;
        this.queryId = queryId;
    }

    @Override
    public ReportDto map(SearchReport searchReport) throws ObjectSerializationException {
        return new ReportDto(queryId, limit, SerializationUtils.serializeObject(searchReport), null, null, page);
    }
}
