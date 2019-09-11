package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.MongoSearchReport;
import kiv.zcu.knowledgeipr.core.model.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;

import java.util.ArrayList;
import java.util.Collections;
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
    public MongoSearchReport search(CategorySearch search) throws UserQueryException, QueryExecutionException {
        MongoSearchReport report = (MongoSearchReport) queryService.getCachedReport(search, MongoSearchReport.class);
        if (report != null) {
            return report;
        }

        // TODO: Count for the case when there are more than 20 confirmed records - more pages
        List<MongoRecord> records = new ArrayList<>();

        List<ReferenceDto> referenceDtos = queryService.getConfirmedRecordsForCategory(search.getCategory());
        referenceDtos = getReferencesListFromRange(referenceDtos, search.getPage(), search.getLimit());
        if (!referenceDtos.isEmpty()) {
            records = dataSearcher.searchByReferences(referenceDtos);
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

        for (MongoRecord record : records) {
            //TODO: Removes the id field from the document so it is not returned back to the user. !!! TMP solution
            record.getDocument().remove("_id");
        }

        report = new MongoSearchReport(records);
        cacheSearch(search, report);

        return report;
    }

    /**
     * Returns a list of references from a specified range, indicated by page and limit parameters
     *
     * @param referenceDtos - The original list from which to extract a new list in range
     * @param page          - The starting number of the range
     * @param limit         - The size of the range
     * @return List constructed from the original references list containing only the elements from the calculated range
     */
    private List<ReferenceDto> getReferencesListFromRange(List<ReferenceDto> referenceDtos, int page, int limit) {
        int beginIndex = (page - 1) * limit;
        int endIndex = beginIndex + limit;
        if (referenceDtos.size() < beginIndex) {
            return Collections.emptyList();
        }

        return referenceDtos.subList(beginIndex, Math.min(referenceDtos.size(), endIndex));
    }
}
