package kiv.zcu.knowledgeipr.core.model.search;

import kiv.zcu.knowledgeipr.core.model.search.category.data.Category;
import kiv.zcu.knowledgeipr.core.model.search.category.tree.TreeNode;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.utils.AppConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class used for creating specific queries.
 */
public class ElasticSearchQueryBuilder {

    private static ElasticSearchQueryBuilder instance = null;

    private ElasticSearchQueryBuilder() {

    }

    public static ElasticSearchQueryBuilder getInstance() {
        if (instance == null) {
            instance = new ElasticSearchQueryBuilder();
        }

        return instance;
    }

    public Query buildOwnersSearchQuery(String ownerName, int year) {
        Map<String, String> filters = new HashMap<>();
        String queryText = "(" + ResponseField.OWNERS_NAME + ":\"" + ownerName + "\"*) AND year:" + year;
        filters.put("$text", queryText);

        Map<String, Object> options = new HashMap<>();
        options.put("timeout", 50);

        return new Query(filters, new HashMap<>(), options);
    }

    public Query buildPatentNumberSearchQuery(String patentNumber) {
        Map<String, String> filters = new HashMap<>();
        filters.put(AppConstants.TEXT_QUERY_KEY, ResponseField.DOCUMENT_ID + ":(+" + patentNumber + "*)");

        Map<String, Object> options = new HashMap<>();
        options.put("timeout", 50);

        return new Query(filters, new HashMap<>(), options);
    }

    public Query buildSimilarDocumentsQuery() {
        HashMap<String, String> filter = new HashMap<>();
        filter.put("null", "null");

        return new Query(filter, new HashMap<>(), new HashMap<>());
    }

    public Query buildPatentsForCategoryQuery(TreeNode<Category> category) {
        Map<String, String> filters = new HashMap<>();
        filters.put("$text", "\"" + category.data.getName() + "\" " + category.data.getKeywordsSeparatedBy(" "));

        return new Query(filters, new HashMap<>(), new HashMap<>());
    }
}
