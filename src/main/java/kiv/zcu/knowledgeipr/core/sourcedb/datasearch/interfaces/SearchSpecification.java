package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;


import kiv.zcu.knowledgeipr.core.model.search.Search;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * Provides a query specification for a single type of query. The child classes
 * create the specifications for concrete target databases.
 *
 * @param <T> - The search instance containing relevant information about a single search,
 *            can be of different types.
 */
public abstract class SearchSpecification<T extends Search> {

    /**
     * Search instance containing relevant information about a search
     */
    protected T search;

    public SearchSpecification(T search) {
        this.search = search;
    }

    /**
     * Returns the constructed query for the target database.
     * todo: change return type to be generic
     * @return - The constructed query
     */
    public abstract QueryBuilder get();

    public T getSearch() {
        return search;
    }
}
