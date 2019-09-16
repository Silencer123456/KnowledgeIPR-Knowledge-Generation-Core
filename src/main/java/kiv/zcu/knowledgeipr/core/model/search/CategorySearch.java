package kiv.zcu.knowledgeipr.core.model.search;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;

/**
 * The specific type of search used when searching in specific categories. Contains
 * additional field holding the category value.
 */
public class CategorySearch extends Search {

    /**
     * Category to be searched
     */
    private String category;

    public CategorySearch(Query query, DataSourceType dataSourceType, int page, int limit, boolean advancedSearch, String category) {
        super(query, dataSourceType, page, limit, advancedSearch);

        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
