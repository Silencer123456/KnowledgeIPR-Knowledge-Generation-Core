package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.logging.Logger;

/**
 * Specifies an ElasticSearch query which runs an advanced text search (uses Lucene's advanced query string).
 *
 * @param <T> The search type relevant to the specification
 */
public class AdvancedTextSearchElasticSpecification<T extends Search> extends SearchSpecification<T> {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public AdvancedTextSearchElasticSpecification(T search) {
        super(search);
    }

    // TODO: change return value to generic type
    @Override
    public QueryBuilder get() {
        super.addSourceSpecificValues();

        LOGGER.info("Running advanced ElasticSearch query string");

        //List<String> queryFields = search.getQuery().getFields();
        //Map<String, Float> fieldsMap = getAllMappedFields(queryFields);

        return QueryBuilders.queryStringQuery(search.getQuery().getTextFilter());
    }
}
