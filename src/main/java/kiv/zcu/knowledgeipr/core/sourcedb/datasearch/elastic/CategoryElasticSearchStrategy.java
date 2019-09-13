package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.ElasticSearchReport;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.model.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class CategoryElasticSearchStrategy extends SearchStrategy<CategorySearch, IElasticDataSearcher> {

    public CategoryElasticSearchStrategy(IElasticDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService);
    }

    @Override
    public SearchReport search(CategorySearch search) throws UserQueryException, QueryExecutionException {
        ElasticSearchReport report = (ElasticSearchReport) queryService.getCachedReport(search, ElasticSearchReport.class);
        if (report != null) {
            return report;
        }

        List<ElasticRecord> records = new ArrayList<>();

        List<ReferenceDto> referenceDtos = queryService.getConfirmedRecordsForCategory(search.getCategory());
        referenceDtos = Common.getListFromRange(referenceDtos, search.getPage(), search.getLimit());
        if (!referenceDtos.isEmpty()) {
            records = dataSearcher.searchByReferences(referenceDtos, search);
        }

        if (records.size() < search.getLimit()) {
            List<ElasticRecord> searchedRecords = dataSearcher.searchData(search);
            // remove records already found in confirmed results in db
            searchedRecords.removeAll(records);

            // Add searched documents after the confirmed ones, but only until the specified limit is reached
            for (ElasticRecord record : searchedRecords) {
                if (records.size() >= search.getLimit()) {
                    break;
                }
                records.add(record);
            }
        }

        report = new ElasticSearchReport(records);
        cacheSearch(search, report);

        return report;
    }
}
