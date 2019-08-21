package kiv.zcu.knowledgeipr.rest.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.dataaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.SearchStrategy;
import kiv.zcu.knowledgeipr.core.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.search.Query;
import kiv.zcu.knowledgeipr.core.search.category.data.Category;
import kiv.zcu.knowledgeipr.core.search.category.data.CategoryHandler;
import kiv.zcu.knowledgeipr.core.search.category.tree.TreeNode;
import kiv.zcu.knowledgeipr.rest.errorhandling.ApiException;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;

import javax.ws.rs.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/category/")
public class CategoryRestService {

    private DataAccessController dataAccessController;

    private SearchStrategy<CategorySearch> searchStrategy;

    public CategoryRestService(DataAccessController dataAccessController, SearchStrategy searchStrategy) {
        this.dataAccessController = dataAccessController;

        this.searchStrategy = searchStrategy;
    }

    private CategoryHandler categories = new CategoryHandler();

    /**
     * Returns a set of results by a category name.
     * Does a search of the database for the specified text.
     *
     * @param categoryName - Name of the category
     * @return
     * @throws ApiException If the category name is not valid, an errorhandling is thrown
     */
    @GET
    @Path("/get/{categoryName}/{page}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getResultsForCategory(@PathParam("categoryName") String categoryName, @PathParam("page") int page) throws ApiException {
        if (!categories.containsCategory(categoryName)) {
            throw new ApiException("Wrong category name.");
        }

        TreeNode<Category> category = categories.getCategory(categoryName);

        Map<String, String> filters = new HashMap<>();
        filters.put("$text", category.data.getKeywordsSeparatedBy(" "));

        Query query = new Query(DataSourceType.PATENT.value, filters, new HashMap<>(), new HashMap<>());

        StandardResponse standardResponse = dataAccessController.search(searchStrategy,
                new CategorySearch(query, page, 20, true, category.data.getName()));

        return javax.ws.rs.core.Response.ok().entity(new Gson().toJson(standardResponse)).build();
    }

    /**
     * Returns a list of categories from a specified tree level.
     *
     * @param treeLevel - Level of the tree from which to return the categories
     * @return
     * @throws ObjectSerializationException If the response cannot be serialized
     */
    @GET
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
    @Path("/tree/plaintext")
    public javax.ws.rs.core.Response getCategoryTreeFromNameAsPlaintext(@QueryParam("name") String categoryName) {
        String treeString = categories.getSubtreeAsString(categoryName);

        return javax.ws.rs.core.Response.ok().entity(treeString).build();
    }

    @GET
    @Path("/tree/json")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCategoryTreeFromNameAsJson(@QueryParam("name") String categoryName) throws ObjectSerializationException {
        String json = categories.getTreeAsJson(categoryName);
        return javax.ws.rs.core.Response.ok().entity(json).build();
    }
}