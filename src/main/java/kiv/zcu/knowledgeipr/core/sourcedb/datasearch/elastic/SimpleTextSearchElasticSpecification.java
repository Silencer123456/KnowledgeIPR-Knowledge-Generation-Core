package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Specifies an ElasticSearch query which runs a text search (now uses Lucene syntax's query string).
 *
 * @param <T> The search type relevant to the specification
 */
public class SimpleTextSearchElasticSpecification<T extends Search> extends SearchSpecification<T> {

    public SimpleTextSearchElasticSpecification(T search) {
        super(search);
    }

    // TODO: change return value to generic type
    @Override
    public QueryBuilder get() {
        Map<String, Float> fieldsMap = new HashMap<>();
        List<String> queryFields = search.getQuery().getFields();

        if (queryFields.isEmpty()) {
            fieldsMap.put("title", 1F);
            fieldsMap.put("abstract", 1F);
        } else {
            for (String field : queryFields) {
                fieldsMap.put(field, 1F);
            }
        }

        return QueryBuilders.simpleQueryStringQuery(search.getQuery().getTextFilter()).fields(fieldsMap);
    }
}
