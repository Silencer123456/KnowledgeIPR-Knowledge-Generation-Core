package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Contains common methods while running ElasticSearch queries
 */
public class CommonElasticRunner {

    private RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http"),
                    new HttpHost("localhost", 9201, "http")));

    /**
     * Runs the specified query builder on the target collection.
     *
     * @param collectionName - Name of the collection/index on which to run the query
     * @param queryBuilder   - The Query Builder containing the query filters
     * @param search         - The search instance
     * @return List of ElasticSearch records
     */
    DbElasticReport runQuery(String collectionName, QueryBuilder queryBuilder, final Search search) {
        DbElasticReport report = new DbElasticReport();
        List<ElasticRecord> records = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(collectionName); // TODO: get name of db from mongo or elastic
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(new TimeValue(search.getQuery().getOptions().getTimeout(), TimeUnit.SECONDS));
        searchSourceBuilder.from((search.getPage() - 1) * search.getLimit());
        searchSourceBuilder.size(search.getLimit());
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                records.add(new ElasticRecord(sourceAsMap));
            }

            report.setDocsCount(hits.getTotalHits().value);

        } catch (IOException e) {
            e.printStackTrace();
        }

        report.setRecords(records);

        return report;
    }
}
