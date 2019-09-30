package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.utils.AppConstants;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ElasticDataSearcher implements IElasticDataSearcher {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonElasticRunner elasticRunner;

    public ElasticDataSearcher() {
        elasticRunner = CommonElasticRunner.getInstance();
    }

    @Override
    public List<ElasticRecord> searchByReferences(List<ReferenceDto> references, Search search) {
        List<String> urls = references
                .stream()
                .filter(object -> ObjectId.isValid(object.getUrl()))
                .map(ReferenceDto::getUrl)
                .collect(Collectors.toList());

        if (urls.isEmpty()) {
            return new ArrayList<>();
        }

        QueryBuilder queryBuilder = QueryBuilders.termsQuery("_id", urls);

        DbElasticReport dbReport = elasticRunner.runQuery(AppConstants.ELASTIC_INDEX_PREFIX + search.getDataSourceType().value, queryBuilder, search);

        return dbReport.getRecords();
    }

    @Override
    public DbElasticReport search(SearchSpecification searchSpecification) {
        Search search = searchSpecification.getSearch();
        QueryBuilder queryBuilder = searchSpecification.get();
        LOGGER.info("Running ElasticSearch query: " + queryBuilder.toString());

        return elasticRunner.runQuery(AppConstants.ELASTIC_INDEX_PREFIX + search.getDataSourceType(), queryBuilder, search);
    }
}
