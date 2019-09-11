package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Query;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Map;

public class ElasticDataSearcher implements IElasticDataSearcher {

    private CommonElasticRunner elasticRunner;

    private RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http"),
                    new HttpHost("localhost", 9201, "http")));

    public ElasticDataSearcher() {
        elasticRunner = new CommonElasticRunner();
    }

    @Override
    public void searchData(Query query) {

        // MatchAll search
        SearchRequest searchRequest = new SearchRequest("knowingipr.patent");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

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
