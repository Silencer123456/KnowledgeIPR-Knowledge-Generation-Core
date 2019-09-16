package kiv.zcu.knowledgeipr.core.model.search;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;

/**
 * Represents an instance of a single search, including the query to be run
 * and other information about the search.
 */
public class Search {

    /**
     * The main search containing filters and conditions
     */
    protected Query query;

    /**
     * The type of data on which the search should be performed
     */
    private DataSourceType dataSourceType;

    /**
     * Which page of results is to be returned by the search
     */
    private int page;

    /**
     * Limit of the results to return from the search
     */
    private int limit;

    /**
     * Indicates if it is an advanced search or not
     */
    private boolean advancedSearch;

    public Search(Query query, DataSourceType dataSourceType, int page, int limit, boolean advancedSearch) {
        this.query = query;
        this.dataSourceType = dataSourceType;
        this.page = page;
        this.limit = limit;
        this.advancedSearch = advancedSearch;
    }

    public Query getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public boolean isFirstPage() {
        return page == 1;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isAdvancedSearch() {
        return advancedSearch;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }
}
