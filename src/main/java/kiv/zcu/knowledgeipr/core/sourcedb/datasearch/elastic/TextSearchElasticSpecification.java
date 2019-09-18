package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Specifies an ElasticSearch query which runs a text search using Lucene syntax.
 *
 * @param <T>
 */
public class TextSearchElasticSpecification<T extends Search> extends SearchSpecification<T> {

    public TextSearchElasticSpecification(T search) {
        super(search);
    }

    // TODO: change to generic type
    @Override
    public QueryBuilder get() {
        return QueryBuilders.queryStringQuery(search.getQuery().getTextFilter());
    }
}
