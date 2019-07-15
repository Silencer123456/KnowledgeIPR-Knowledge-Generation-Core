package kiv.zcu.knowledgeipr.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kiv.zcu.knowledgeipr.core.mongo.DataSourceType;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.core.query.category.data.Category;
import kiv.zcu.knowledgeipr.core.query.category.tree.SampleCategories;
import kiv.zcu.knowledgeipr.core.query.category.tree.TreeNode;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.report.ReportCreator;
import kiv.zcu.knowledgeipr.rest.exception.ApiException;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;
import kiv.zcu.knowledgeipr.rest.response.BaseResponse;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;
import kiv.zcu.knowledgeipr.rest.response.StatusResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/category/")
public class CategoryRestService {

    private ReportController reportGenerator = new ReportController(new ReportCreator());

    private SampleCategories categories = new SampleCategories();

    /**
     * Returns a set of results by a category name.
     * Does a search of the database for the specified text.
     *
     * @param categoryName - Name of the category
     * @return
     * @throws ApiException If the category name is not valid, an exception is thrown
     */
    @GET
    @Path("/get/{categoryName}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getResultsForCategory(@PathParam("categoryName") String categoryName) throws ApiException {
        if (!categories.containsCategory(categoryName)) {
            throw new ApiException(new BaseResponse(StatusResponse.ERROR, "Wrong category name."));
        }

        TreeNode<Category> category = categories.getCategory(categoryName);

        Map<String, String> filters = new HashMap<>();
        filters.put("$text", category.data.getKeywordsSeparatedBy(" "));

        Query query = new Query(DataSourceType.PATENT.value, filters, new HashMap<>(), new HashMap<>());

        StandardResponse standardResponse = reportGenerator.processQuery(query, 1, 20);

        return javax.ws.rs.core.Response.ok().entity(new Gson().toJson(standardResponse)).build();
    }

    /**
     * Returns a list of categories from a specified tree level.
     *
     * @param treeLevel - Level of the tree from which to return the categories
     * @return
     * @throws ResponseSerializationException If the response cannot be serialized
     */
    @GET
    @Path("/tree/{level}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCategoryTree(@PathParam("level") int treeLevel) throws ResponseSerializationException {
        List<String> tree = categories.getNodesAtLevel(treeLevel);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return javax.ws.rs.core.Response.ok().entity(mapper.writeValueAsString(tree)).build();

        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseSerializationException(new BaseResponse(StatusResponse.ERROR, e.getMessage()));
        }
    }

    /**
     * Returns the whole category tree as string
     *
     * @return
     * @throws ResponseSerializationException If the response cannot be serialized
     */
    @GET
    @Path("/tree")
    public javax.ws.rs.core.Response getCategoryTree() throws ResponseSerializationException {
        String treeString = categories.getTreeAsString();
        return javax.ws.rs.core.Response.ok().entity(treeString).build();
    }
}
