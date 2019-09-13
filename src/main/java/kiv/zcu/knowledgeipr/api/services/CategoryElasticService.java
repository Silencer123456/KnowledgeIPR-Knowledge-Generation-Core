package kiv.zcu.knowledgeipr.api.services;

import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.IElasticDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;

import javax.ws.rs.Path;

@Path("/category/")
public class CategoryElasticService extends CategoryService<IElasticDataSearcher> {

    public CategoryElasticService(DataAccessController dataAccessController, SearchStrategy<CategorySearch, IElasticDataSearcher> searchStrategy) {
        super(dataAccessController, searchStrategy);
    }
}
