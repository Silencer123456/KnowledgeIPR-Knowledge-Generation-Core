package kiv.zcu.knowledgeipr.api.services;

import kiv.zcu.knowledgeipr.api.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.ElasticSearchQueryBuilder;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.IElasticDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification.AdvancedTextSearchElasticSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification.SimilarSearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.utils.AppConstants;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Logged
@Path("/search/")
public class SearchElasticService extends SearchService<IElasticDataSearcher> {

    private ElasticSearchQueryBuilder queryBuilder;

    public SearchElasticService(DataAccessController dataAccessController, SearchStrategy<Search, IElasticDataSearcher> searchStrategy) {
        super(dataAccessController, searchStrategy);
        queryBuilder = ElasticSearchQueryBuilder.getInstance();
    }

    @Override
    public Response ownersSearch(int page, String ownerName, int year) throws ApiException, ObjectSerializationException {
        isPageValid(page);

        Query query = queryBuilder.buildOwnersSearchQuery(ownerName, year);

        Search search = new Search(query, DataSourceType.PATENT, page, AppConstants.RESULTS_LIMIT, false, searchStrategy.getSearchEngineName());

        SearchSpecification<Search> searchSpecification = new AdvancedTextSearchElasticSpecification<>(search);
        return initSearch(searchSpecification);
    }

    @Override
    public Response patentNumberSearch(String patentNumber) throws ApiException, ObjectSerializationException {
        if (!patentNumber.matches("[a-zA-Z0-9 ]*")) {
            throw new ApiException("Patent number must contain only letters and numbers");
        }


        Query query = queryBuilder.buildPatentNumberSearchQuery(patentNumber);

        Search search = new Search(query, DataSourceType.PATENT, 1, AppConstants.RESULTS_LIMIT, false, searchStrategy.getSearchEngineName());
        SearchSpecification<Search> searchSpecification = new AdvancedTextSearchElasticSpecification<>(search);

        return initSearch(searchSpecification);
    }

    @POST
    @Logged
    @Path("/similar")
    @Consumes("application/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response searchSimilar(@QueryParam("page") int page, @QueryParam("id") String id)
            throws ApiException, ObjectSerializationException {

        isPageValid(page);
        Search search = new Search(queryBuilder.buildSimilarDocumentsQuery(id), DataSourceType.PATENT, page, AppConstants.RESULTS_LIMIT, false, searchStrategy.getSearchEngineName());

        SearchSpecification<Search> searchSpecification = new SimilarSearchSpecification(search, id);// TODO: Mongo does not make use of the search specifications

        return initSearch(searchSpecification);
    }
}
