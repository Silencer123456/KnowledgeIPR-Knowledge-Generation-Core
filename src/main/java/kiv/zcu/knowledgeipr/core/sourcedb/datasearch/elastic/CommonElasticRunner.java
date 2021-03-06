package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.DataSourceManager;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
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
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

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
                            .setConnectTimeout(100000)
                            .setSocketTimeout(6000000)));

    public static CommonElasticRunner getInstance() {
        if (instance == null) {
            instance = new CommonElasticRunner();
        }

        return instance;
    }

    public void closeClient() throws IOException {
        client.close();
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

        //TODO: Extract method
        String sort = search.getQuery().getFilters().get("sort");
        if (sort != null) {
            String[] arr = sort.split(":");
            if (!ResponseField.isValid(arr[0])) {
                throw new QueryExecutionException("The sorting field: " + arr[0] + " is not valid.");
            }
            if (arr.length == 2) {
                SortOrder order = arr[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
                searchSourceBuilder.sort(new FieldSortBuilder(arr[0]).order(order));
            }
        }

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
                String dataSource = (String) sourceAsMap.get(ResponseField.DATA_SOURCE.value);
                if (dataSource != null) {
                    sourceAsMap.put("dataCategory", DataSourceManager.getTypeForDataSource(dataSource));
                }

                records.add(new ElasticRecord(sourceAsMap));
            }

            long value = hits.getTotalHits().value;
            report.setDocsCount(value > 2000 ? 2001 : value);
            report.setSearchedIndexes(indexes);

        } catch (IOException | ElasticsearchException e) {
            e.printStackTrace();
            throw new QueryExecutionException(e.getMessage());
        }

        report.setRecords(records);

        return report;
    }

    /**
     * Creates a search response and returns created Aggregations object with agg results.
     *
     * @param indexes - The list of indexes to search
     * @param aggregationBuilders - The ElasticSearch builder with specified aggregations
     * @return aggregation results. If the aggregation fails, null is returned
     */
    Aggregations runAggregation(List<String> indexes, AggregationBuilder aggregationBuilders) throws QueryExecutionException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.aggregation(aggregationBuilders);

        return getSearchResponse(indexes, searchSourceBuilder, aggregationBuilders);
    }

    Aggregations runAggregation(List<String> indexes, AggregationBuilder aggregationBuilders, QueryBuilder query) throws QueryExecutionException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(query);
        searchSourceBuilder.aggregation(aggregationBuilders);

        return getSearchResponse(indexes, searchSourceBuilder, aggregationBuilders);
    }

    Aggregations getSearchResponse(List<String> indexes, SearchSourceBuilder searchSourceBuilder, AggregationBuilder aggregationBuilders) throws QueryExecutionException {
        Aggregations agg = null;

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
