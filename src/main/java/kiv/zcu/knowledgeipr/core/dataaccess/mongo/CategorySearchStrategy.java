package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import kiv.zcu.knowledgeipr.core.database.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.report.DataReport;
import kiv.zcu.knowledgeipr.core.search.CategorySearch;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

import java.util.List;

/**
 * Provides implementation of the searching process using a category search strategy in the Mongo database.
 */
public class CategorySearchStrategy extends SearchStrategy<CategorySearch> {

    public CategorySearchStrategy(IMongoDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService);
    }

    /**
     * {@inheritDoc}
     * First cached reports are searched.
     * Then the list of confirmed documents for the category is retrieved from the database.
     * The list of document references is retrieved from Mongo database.
     * The rest of the results are retrieved by regular search.
     */
    @Override
    public DataReport search(CategorySearch search) throws UserQueryException {
        DataReport report = queryService.getCachedReport(search);
        if (report != null) {
            return report;
        }

        List<ReferenceDto> referenceDtos = queryService.getConfirmedRecordsForCategory(search.getCategory());

        List<DbRecord> records;
        records = dataSearcher.searchByReferences(referenceDtos);

//        if (search.isAdvancedSearch()) {
//            records = dataSearcher.runSearchAdvanced(search.getQuery(), search.getPage(), search.getLimit());
//        } else {
//            records = dataSearcher.runSearchSimple(search.getQuery(), search.getPage(), search.getLimit());
//        }

        report = new DataReport(records);

        cacheSearch(search, report);

        return report;
    }
}
