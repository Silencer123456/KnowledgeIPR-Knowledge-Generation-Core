package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.ElasticSearchReport;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.model.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.model.search.SearchEngineName;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class CategoryElasticSearchStrategy extends SearchStrategy<CategorySearch, IElasticDataSearcher> {

    public CategoryElasticSearchStrategy(IElasticDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService, SearchEngineName.elastic);
    }

    @Override
    public SearchReport search(SearchSpecification<CategorySearch> searchSpecification) throws UserQueryException, QueryExecutionException {
        CategorySearch search = searchSpecification.getSearch();

        ElasticSearchReport report = (ElasticSearchReport) queryService.getCachedReport(search, ElasticSearchReport.class);
        if (report != null) {
            return report;
        }

        DbElasticReport dbReport = new DbElasticReport();
        List<ElasticRecord> records = new ArrayList<>();

        List<ReferenceDto> referenceDtos = queryService.getConfirmedRecordsForCategory(search.getCategory());
        //long recordsInCategory = queryService.getTotalRecordsForCategory(search.getCategory());

        referenceDtos = Common.getListFromRange(referenceDtos, search.getPage(), search.getLimit());
        if (!referenceDtos.isEmpty()) {
            records = dataSearcher.searchByReferences(referenceDtos, search);
        }

        if (records.size() < search.getLimit()) {
            dbReport = dataSearcher.search(searchSpecification);
            //dbReport = dataSearcher.searchData(search);
            // remove records already found in confirmed results in db
            dbReport.getRecords().removeAll(records);

            // Add searched documents after the confirmed ones, but only until the specified limit is reached
            for (ElasticRecord record : dbReport.getRecords()) {
                if (records.size() >= search.getLimit()) {
                    break;
                }
                records.add(record);
            }
        }

        report = new ElasticSearchReport(records, dbReport.getDocsCount());
        //report.setDocsCount(dbReport.getDocsCount() == 0 ? recordsInCategory : dbReport.getDocsCount());
        cacheSearch(search, report);

        return report;
    }
}
