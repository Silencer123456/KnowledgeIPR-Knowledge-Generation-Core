package kiv.zcu.knowledgeipr.api.services;


import kiv.zcu.knowledgeipr.api.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.SimpleTextSearchElasticSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.IMongoDataSearcher;
import kiv.zcu.knowledgeipr.utils.AppConstants;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Logged
@Path("/search/mongo")
public class SearchMongoService extends SearchService<IMongoDataSearcher> {

    public SearchMongoService(DataAccessController dataAccessController, SearchStrategy<Search, IMongoDataSearcher> searchStrategy) {
        super(dataAccessController, searchStrategy);
    }

    @Override
    public Response ownersSearch(int page, String ownerName, int year) throws ApiException, ObjectSerializationException {
        {
            isPageValid(page);

            Map<String, String> filters = new HashMap<>();
            filters.put(ResponseField.OWNERS_NAME.value, ownerName);

            Map<String, Map<String, Integer>> conditions = new HashMap<>();

            Map<String, Integer> yearMap = new HashMap<>();
            if (year > 0) {
                yearMap.put("$eq", year);
                conditions.put(ResponseField.YEAR.value, yearMap);
            }

            Map<String, Object> options = new HashMap<>();
            options.put("timeout", 50);

            Query query = new Query(filters, conditions, options);
            Search search = new Search(query, DataSourceType.PATENT, page, AppConstants.RESULTS_LIMIT, false, searchStrategy.getSearchEngineName());
            SearchSpecification<Search> searchSpecification = new SimpleTextSearchElasticSpecification<>(search); // TODO: Mongo does not make use of search specifications so far

            return initSearch(searchSpecification);
        }
    }

    @Override
    public Response patentNumberSearch(String patentNumber) throws ApiException, ObjectSerializationException {
        {
            Map<String, String> filters = new HashMap<>();
            filters.put(ResponseField.DOCUMENT_ID.value, patentNumber);

            Map<String, Object> options = new HashMap<>();
            options.put("timeout", 50);

            Query query = new Query(filters, new HashMap<>(), options);
            Search search = new Search(query, DataSourceType.PATENT, 1, AppConstants.RESULTS_LIMIT, false, searchStrategy.getSearchEngineName());
            SearchSpecification<Search> searchSpecification = new SimpleTextSearchElasticSpecification<>(search);

            return initSearch(searchSpecification);
        }
    }
}
