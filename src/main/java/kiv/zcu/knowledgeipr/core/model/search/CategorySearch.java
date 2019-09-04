package kiv.zcu.knowledgeipr.core.model.search;

/**
 * The specific type of search used when searching in specific categories. Contains
 * additional field holding the category value.
 */
public class CategorySearch extends Search {

    /**
     * Category to be searched
     */
    private String category;

    public CategorySearch(Query query, int page, int limit, boolean advancedSearch, String category) {
        super(query, page, limit, advancedSearch);

        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
