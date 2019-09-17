package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;


import kiv.zcu.knowledgeipr.core.model.search.Search;
import org.elasticsearch.index.query.QueryBuilder;

//TODO: For now for ElasticSearch, later change!!!!!!!!!
public abstract class SearchSpecification<T extends Search> {

    protected T search;

    public SearchSpecification(T search) {
        this.search = search;
    }

    public abstract QueryBuilder get();

    public T getSearch() {
        return search;
    }
}
