package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Query;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ElasticDataSearcher implements IElasticDataSearcher {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonElasticRunner elasticRunner;

    private RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http"),
                    new HttpHost("localhost", 9201, "http")));

    public ElasticDataSearcher() {
        elasticRunner = new CommonElasticRunner();
    }

    @Override
    public List<ElasticRecord> searchData(Query query) {

        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query.getTextFilter());

        LOGGER.info("Running : " + queryBuilder.toString());

        List<ElasticRecord> records = new ArrayList<>();

        // MatchAll search
        SearchRequest searchRequest = new SearchRequest("knowingipr." + query.getSourceType());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(new TimeValue(query.getOptions().getTimeout(), TimeUnit.SECONDS));
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                records.add(new ElasticRecord(sourceAsMap)); // TODO: move to separate query runner class
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;

//        GetRequest getRequest = new GetRequest("knowingipr.patent", "5d2dc2795c81a41214c5e362");
//
//        try {
//            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
//            if (getResponse.isExists()) {
//                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }  catch (ElasticsearchException e) {
//            if (e.status() == RestStatus.NOT_FOUND) {
//
//            }
//            if (e.status() == RestStatus.CONFLICT) {
//
//            }
//        }
    }
}
