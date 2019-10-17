package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
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
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Contains common methods while running ElasticSearch queries
 */
public class CommonElasticRunner {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static CommonElasticRunner instance;

    private CommonElasticRunner() {
    }

    private RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http"),
                    new HttpHost("localhost", 9201, "http")).setRequestConfigCallback(
                    requestConfigBuilder -> requestConfigBuilder
                            .setConnectTimeout(10000)
                            .setSocketTimeout(60000)));

    public static CommonElasticRunner getInstance() {
        if (instance == null) {
            return new CommonElasticRunner();
        }

        return instance;
    }

    DbElasticReportWrapper runQuery(QueryBuilder queryBuilder, final Search search, String index) throws QueryExecutionException {
        return runQuery(queryBuilder, search, new ArrayList<String>() {{
            add(index);
        }});
    }

    /**
     * Runs the specified query builder on the target collection.
     *
     * @param indexes - Names of the indexes on which to run the query
     * @param queryBuilder   - The Query Builder containing the query filters
     * @param search         - The search instance
     * @return List of ElasticSearch records
     */
    DbElasticReportWrapper runQuery(QueryBuilder queryBuilder, final Search search, List<String> indexes) throws QueryExecutionException {
        DbElasticReportWrapper report = new DbElasticReportWrapper();
        List<ElasticRecord> records = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest(indexes.toArray(new String[0]));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(new TimeValue(search.getQuery().getOptions().getTimeout(), TimeUnit.SECONDS));
        searchSourceBuilder.from((search.getPage() - 1) * search.getLimit());
        searchSourceBuilder.size(search.getLimit());
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);

        try {
            LOGGER.info("Running ElasticSearch query: " + queryBuilder.toString());
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            report.setTimeValue(searchResponse.getTook().getStringRep());

            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                sourceAsMap.put("_id", hit.getId());
                sourceAsMap.put("_score", hit.getScore());

                records.add(new ElasticRecord(sourceAsMap));
            }

            report.setDocsCount(hits.getTotalHits().value);
            report.setSearchedIndexes(indexes);

        } catch (IOException e) {
            e.printStackTrace();

            throw new QueryExecutionException(e.getMessage());
        }

        report.setRecords(records);

        return report;
    }

    /**
     * Creates a search response and returns created Aggregations object with agg results.
     *
     * @param indexName - The name of the index to search
     * @param aggregationBuilders - The ElasticSearch builder with specified aggregations
     * @return aggregation results. If the aggregation fails, null is returned
     */
    Aggregations runAggregation(List<String> indexes, AggregationBuilder aggregationBuilders) throws QueryExecutionException {
        Aggregations agg = null;

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.aggregation(aggregationBuilders);

        SearchRequest searchRequest = new SearchRequest(indexes.toArray(new String[0]));
        searchRequest.source(searchSourceBuilder);

        try {
            LOGGER.info("Running ElasticSearch query: " + aggregationBuilders.toString());

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            agg = searchResponse.getAggregations();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (agg == null) {
            throw new QueryExecutionException("Aggregation " + aggregationBuilders.toString() + " failed.");
        }

        return agg;
    }
}
