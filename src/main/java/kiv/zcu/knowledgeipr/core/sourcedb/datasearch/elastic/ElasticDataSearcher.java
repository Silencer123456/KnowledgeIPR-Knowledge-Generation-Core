package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 */
public class ElasticDataSearcher implements IElasticDataSearcher {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final String patentIndexPrefix = "knowingipr.";

    private CommonElasticRunner elasticRunner;

    public ElasticDataSearcher() {
        elasticRunner = new CommonElasticRunner();
    }

    @Override
    public List<ElasticRecord> searchData(Search search) {
        Query query = search.getQuery();

        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query.getTextFilter());

        LOGGER.info("Running : " + queryBuilder.toString());

        return elasticRunner.runQuery(patentIndexPrefix + query.getSourceType(), queryBuilder, search);
    }

    @Override
    public List<ElasticRecord> searchByReferences(List<ReferenceDto> references, Search search) {
        // To list of ObjectIds
        List<ObjectId> urls = references
                .stream()
                .filter(object -> ObjectId.isValid(object.getUrl()))
                .map(object -> new ObjectId(object.getUrl()))
                .collect(Collectors.toList());

        if (urls.isEmpty()) {
            return new ArrayList<>();
        }

        QueryBuilder queryBuilder = QueryBuilders.termsQuery("_id", urls);

        return elasticRunner.runQuery(patentIndexPrefix + search.getQuery().getSourceType().value, queryBuilder, search);
    }
}
