package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

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
        LOGGER.info("Running simple ElasticSearch query string");

        List<String> queryFields = search.getQuery().getFields();
        Map<String, Float> fieldsMap = getAllMappedFields(queryFields);

        return QueryBuilders.simpleQueryStringQuery(search.getQuery().getTextFilter()).fields(fieldsMap);
    }
}
