package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import kiv.zcu.knowledgeipr.core.database.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.report.SearchReport;
import kiv.zcu.knowledgeipr.core.search.CategorySearch;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

import java.util.ArrayList;
import java.util.List;

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
    public SearchReport search(CategorySearch search) throws UserQueryException {
        SearchReport report = queryService.getCachedReport(search);
        if (report != null) {
            return report;
        }

        // TODO: Count for the case when there are more than 20 confirmed records - more pages
        List<MongoRecord> records = new ArrayList<>();

        List<ReferenceDto> referenceDtos = queryService.getConfirmedRecordsForCategory(search.getCategory());
        if (search.isFirstPage()) { // Get confirmed only if the first page is requested
            if (!referenceDtos.isEmpty()) {
                records = dataSearcher.searchByReferences(referenceDtos);
            }
        }

        if (records.size() < search.getLimit()) {
            List<MongoRecord> searchedRecords = dataSearcher.runSearchAdvanced(search.getQuery(), search.getPage(), search.getLimit());

            // remove records already found in confirmed results in db
            searchedRecords.removeAll(records);

            // Add searched documents after the confirmed ones, but only until the specified limit is reached
            for (MongoRecord record : searchedRecords) {
                if (records.size() >= search.getLimit()) {
                    break;
                }
                records.add(record);
            }
        }

        report = new SearchReport(records);
        cacheSearch(search, report);

        return report;
    }
}
