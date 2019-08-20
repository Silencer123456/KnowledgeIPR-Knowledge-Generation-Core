package kiv.zcu.knowledgeipr.core.controller;

import kiv.zcu.knowledgeipr.core.dataaccess.DbRecord;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.IDataSearcher;
import kiv.zcu.knowledgeipr.core.database.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.query.Search;
import kiv.zcu.knowledgeipr.core.report.DataReport;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

import java.util.List;

/**
 * Provides implementation of search using a category search strategy.
 */
public class CategorySearchStrategy extends SearchStrategy {

    public CategorySearchStrategy(IDataSearcher dataSearcher, DbQueryService queryService) {
        super(dataSearcher, queryService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataReport search(Search search) throws UserQueryException {
        DataReport report = queryService.getCachedReport(search);
        if (report != null) {
            return report;
        }

        //TODO: !!! get from SQL database confirmed records for the specific category, take the rest from Mongo

        List<ReferenceDto> referenceDtos = queryService.getConfirmedRecordsForCategory("rims"); //TODO: get the category name from the user's request

        //TODO:  Retrieve actual documents from the references from target database


        List<DbRecord> records;
        if (search.isAdvancedSearch()) {
            records = dataSearcher.runSearchAdvanced(search.getQuery(), search.getPage(), search.getLimit());
        } else {
            records = dataSearcher.runSearchSimple(search.getQuery(), search.getPage(), search.getLimit());
        }

        report = new DataReport(records);

        cacheSearch(search, report);

        return report;
    }
}
