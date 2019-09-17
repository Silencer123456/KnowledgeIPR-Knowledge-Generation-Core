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
        elasticRunner = new CommonElasticRunner();
    }

//    public DbElasticReport searchData(Search search) {
//        Query query = search.getQuery();
//
//        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query.getTextFilter());
//
//        LOGGER.info("Running : " + queryBuilder.toString());
//
//        return elasticRunner.runQuery(patentIndexPrefix + search.getDataSourceType(), queryBuilder, search);
//    }

//    @Override
//    public DbElasticReport searchSimilar(Search search, String id) {
//        QueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(new String[] {"title"}, null,
//                new MoreLikeThisQueryBuilder.Item[] {new MoreLikeThisQueryBuilder.Item(search.getDataSourceType().value, id)})
//                .minTermFreq(1)
//                .minDocFreq(1);
//
//        LOGGER.info("Running : " + queryBuilder.toString());
//
//        return elasticRunner.runQuery(patentIndexPrefix + search.getDataSourceType(), queryBuilder, search);
//    }

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

        DbElasticReport dbReport = elasticRunner.runQuery(AppConstants.PATENT_ELASTIC_PREFIX + search.getDataSourceType().value, queryBuilder, search);

        return dbReport.getRecords();
    }

    @Override
    public DbElasticReport search(SearchSpecification searchSpecification) {
        Search search = searchSpecification.getSearch();
        QueryBuilder queryBuilder = searchSpecification.get();
        LOGGER.info("Running ElasticSearch query: " + queryBuilder.toString());

        return elasticRunner.runQuery(AppConstants.PATENT_ELASTIC_PREFIX + search.getDataSourceType(), queryBuilder, search);
    }
}
