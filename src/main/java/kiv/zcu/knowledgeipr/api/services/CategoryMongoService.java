package kiv.zcu.knowledgeipr.api.services;

import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.IMongoDataSearcher;

import javax.ws.rs.Path;

@Path("/category/mongo/")
public class CategoryMongoService extends CategoryService<IMongoDataSearcher> {
    public CategoryMongoService(DataAccessController dataAccessController, SearchStrategy<CategorySearch, IMongoDataSearcher> searchStrategy) {
        super(dataAccessController, searchStrategy);
    }
}
