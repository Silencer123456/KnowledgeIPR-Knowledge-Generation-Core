package kiv.zcu.knowledgeipr.core.search;

public class CategorySearch extends Search {

    private String category;

    public CategorySearch(Query query, int page, int limit, boolean advancedSearch, String category) {
        super(query, page, limit, advancedSearch);

        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
