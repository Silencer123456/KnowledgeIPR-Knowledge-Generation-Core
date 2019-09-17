package kiv.zcu.knowledgeipr.core.model.search;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;

public class SimilarSearch extends Search {

    private String id;

    public SimilarSearch(Query query, DataSourceType dataSourceType, int page, int limit, boolean advancedSearch, SearchEngineName searchEngineName, String id) {
        super(query, dataSourceType, page, limit, advancedSearch, searchEngineName);

        this.id = id;
    }

    public String getId() {
        return id;
    }
}
