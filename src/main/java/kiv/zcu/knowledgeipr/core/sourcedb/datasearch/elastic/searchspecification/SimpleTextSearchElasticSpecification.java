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
import java.util.logging.Logger;

/**
 * Specifies an ElasticSearch query which runs a text search (now uses Lucene syntax's query string).
 *
 * @param <T> The search type relevant to the specification
 */
public class SimpleTextSearchElasticSpecification<T extends Search> extends SearchSpecification<T> {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);


    public SimpleTextSearchElasticSpecification(T search) {
        super(search);
    }

    // TODO: change return value to generic type
    @Override
    public QueryBuilder get() {
        Map<String, Float> fieldsMap = new HashMap<>();
        List<String> queryFields = search.getQuery().getFields();

        if (queryFields.isEmpty()) {
            fieldsMap = getDefaultFieldsMap();

        } else {
            for (String field : queryFields) {
                //TODO: Handle addition of mapped fields here as well
                ResponseField foundField = ResponseField.getNameFromValue(field);
                if (foundField != null) {
                    fieldsMap.put(field, 1F);
                    fieldsMap.put(PatstatMapper.getMapping(foundField), 1F);
                } else {
                    LOGGER.warning("The field " + field + " is not a valid search field and will be skipped.");
                }
            }
        }
        return QueryBuilders.simpleQueryStringQuery(search.getQuery().getTextFilter()).fields(fieldsMap);
    }
}
