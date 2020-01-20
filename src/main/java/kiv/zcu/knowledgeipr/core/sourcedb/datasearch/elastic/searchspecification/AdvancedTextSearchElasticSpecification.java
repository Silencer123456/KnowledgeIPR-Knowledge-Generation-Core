package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Specifies an ElasticSearch query which runs an advanced text search (uses Lucene's advanced query string).
 *
 * @param <T> The search type relevant to the specification
 */
public class AdvancedTextSearchElasticSpecification<T extends Search> extends SearchSpecification<T> {

    public AdvancedTextSearchElasticSpecification(T search) {
        super(search);
    }

    // TODO: change return value to generic type
    @Override
    public QueryBuilder get() {
        return QueryBuilders.queryStringQuery(search.getQuery().getTextFilter());
    }
}
