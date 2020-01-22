package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.PatstatMapper;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
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

        // TODO: Instead of taking the names of fields directly from the query's filter, use mapping for other data sources like PATSTAT
        if (queryFields.isEmpty()) {
            fieldsMap.put(ResponseField.TITLE.value, 1F);
            fieldsMap.put(ResponseField.ABSTRACT.value, 1F);
            fieldsMap.put(PatstatMapper.getMapping(ResponseField.TITLE), 1F);
            fieldsMap.put(PatstatMapper.getMapping(ResponseField.ABSTRACT), 1F);



        } else {
            for (String field : queryFields) {
                //TODO: Handle addition of mapped fields here as well
                fieldsMap.put(field, 1F);
            }
        }
        return QueryBuilders.simpleQueryStringQuery(search.getQuery().getTextFilter()).fields(fieldsMap);
    }
}
