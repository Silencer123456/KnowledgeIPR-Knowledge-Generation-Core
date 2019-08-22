package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import kiv.zcu.knowledgeipr.core.database.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.report.DataReport;
import kiv.zcu.knowledgeipr.core.search.CategorySearch;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides implementation of the searching process using a category search strategy in the MONGO database.
 */
public class CategorySearchStrategy extends SearchStrategy<CategorySearch, IMongoDataSearcher> {

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

        // TODO: Count for the case when there are more than 20 confirmed records - more pages
        List<DbRecord> records = new ArrayList<>();
        if (search.isFirstPage()) { // Get confirmed only if the first page is requested
            List<ReferenceDto> referenceDtos = queryService.getConfirmedRecordsForCategory(search.getCategory());
            if (!referenceDtos.isEmpty()) {
                records = dataSearcher.searchByReferences(referenceDtos);
            }
        }

        // TODO:  Ensure there are no duplicate records
        if (records.size() < search.getLimit()) {
            List<DbRecord> searchedRecords = dataSearcher.runSearchAdvanced(search.getQuery(), search.getPage(), search.getLimit() - records.size()); // Search the rest in Mongo
            Optional.ofNullable(searchedRecords).ifPresent(records::addAll);
        }

        report = new DataReport(records);
        cacheSearch(search, report);

        return report;
    }
}
