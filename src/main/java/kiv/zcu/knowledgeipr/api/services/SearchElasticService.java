package kiv.zcu.knowledgeipr.api.services;

import kiv.zcu.knowledgeipr.api.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.IElasticDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.utils.AppConstants;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Logged
@Path("/search/")
public class SearchElasticService extends SearchService<IElasticDataSearcher> {

    public SearchElasticService(DataAccessController dataAccessController, SearchStrategy<Search, IElasticDataSearcher> searchStrategy) {
        super(dataAccessController, searchStrategy);
    }

    @Override
    public Response ownersSearch(int page, String ownerName, int year) throws ApiException, ObjectSerializationException {
        {
            isPageValid(page);

            Map<String, String> filters = new HashMap<>();
            String queryText = "(" + ResponseField.OWNERS_NAME + ":\"" + ownerName + "\"*) AND year:" + year;
            filters.put("$text", queryText);

            Map<String, Object> options = new HashMap<>();
            options.put("timeout", 50);

            Query query = new Query(filters, new HashMap<>(), options);
            return processQueryInit(new Search(query, DataSourceType.PATENT, page, AppConstants.RESULTS_LIMIT, false));
        }
    }

    @Override
    public Response patentNumberSearch(String patentNumber) throws ApiException, ObjectSerializationException {
        {
            Map<String, String> filters = new HashMap<>();
            filters.put(AppConstants.TEXT_QUERY_KEY, ResponseField.DOCUMENT_ID + ":(+" + patentNumber + "*)");

            Map<String, Object> options = new HashMap<>();
            options.put("timeout", 50);

            Query query = new Query(filters, new HashMap<>(), options);
            return processQueryInit(new Search(query, DataSourceType.PATENT, 1, AppConstants.RESULTS_LIMIT, false));
        }
    }
}
