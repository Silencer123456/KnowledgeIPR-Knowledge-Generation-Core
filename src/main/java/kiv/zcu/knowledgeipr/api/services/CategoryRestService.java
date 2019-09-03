package kiv.zcu.knowledgeipr.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import kiv.zcu.knowledgeipr.api.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.api.response.SearchResponse;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.category.data.Category;
import kiv.zcu.knowledgeipr.core.model.search.category.data.CategoryHandler;
import kiv.zcu.knowledgeipr.core.model.search.category.tree.TreeNode;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.IMongoDataSearcher;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import javax.ws.rs.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/category/")
public class CategoryRestService {

    private DataAccessController dataAccessController;

    private SearchStrategy<CategorySearch, IMongoDataSearcher> searchStrategy;

    public CategoryRestService(DataAccessController dataAccessController, SearchStrategy<CategorySearch, IMongoDataSearcher> searchStrategy) {
        this.dataAccessController = dataAccessController;

        this.searchStrategy = searchStrategy;
    }

    private CategoryHandler categories = new CategoryHandler();

    /**
     * Returns a set of patent  by a category name.
     * Does a search of the database for the specified text.
     *
     * @param categoryName - Name of the category
     * @return
     * @throws ApiException If the category name is not valid, an error is thrown
     */
    @GET
    @Logged
    @Path("/get/{categoryName}/{page}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getResultsForCategory(@PathParam("categoryName") String categoryName, @PathParam("page") int page)
            throws ApiException, ObjectSerializationException {
        if (!categories.containsCategory(categoryName)) {
            throw new ApiException("Wrong category name.");
        }

        TreeNode<Category> category = categories.getCategory(categoryName);

        Map<String, String> filters = new HashMap<>();
        filters.put("$text", category.data.getKeywordsSeparatedBy(" "));

        Query query = new Query(DataSourceType.PATENT.value, filters, new HashMap<>(), new HashMap<>());

        SearchResponse searchResponse = dataAccessController.search(searchStrategy,
                new CategorySearch(query, page, 20, true, category.data.getName()));

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(searchResponse)).build();
    }

    /**
     * Returns a list of categories from a specified tree level.
     *
     * @param treeLevel - Level of the tree from which to return the categories
     * @return
     * @throws ObjectSerializationException If the response cannot be serialized
     */
    @GET
    @Logged
    @Path("/tree")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCategoryTree(@QueryParam("level") int treeLevel) throws ObjectSerializationException {
        List<String> tree = categories.getNodesAtLevel(treeLevel);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return javax.ws.rs.core.Response.ok().entity(mapper.writeValueAsString(tree)).build();

        } catch (IOException e) {
            e.printStackTrace();
            throw new ObjectSerializationException(e.getMessage());
        }
    }

    /**
     * Returns the whole category tree as string
     *
     * @return
     * @throws ObjectSerializationException If the response cannot be serialized
     */
    @GET
    @Logged
    @Path("/tree/plaintext")
    public javax.ws.rs.core.Response getCategoryTreeFromNameAsPlaintext(@QueryParam("name") String categoryName) {
        String treeString = categories.getSubtreeAsString(categoryName);

        return javax.ws.rs.core.Response.ok().entity(treeString).build();
    }

    @GET
    @Logged
    @Path("/tree/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCategoryTreeFromNameAsJson(@QueryParam("name") String categoryName) throws ObjectSerializationException {
        String json = categories.getTreeAsJson(categoryName);
        return javax.ws.rs.core.Response.ok().entity(json).build();
    }
}