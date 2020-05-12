package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSource;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;
import kiv.zcu.knowledgeipr.utils.AppConstants;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.Filters;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains implementations of concrete queries specific to ElasticSearch
 */
public class ElasticQueryRunner implements IQueryRunner {

    /**
     * Query runner
     */
    private CommonElasticRunner elasticRunner;

    public ElasticQueryRunner() {
        elasticRunner = CommonElasticRunner.getInstance();
    }

    @Override
    public List<Pair<Object, Long>> countByField(List<DataSource> indexes, ResponseField field, int limit) throws QueryExecutionException {
        return countByStringArrayField(indexes, field, limit);
    }

    @Override
    public List<Pair<Object, Long>> countByStringArrayField(List<DataSource> indexes, ResponseField field, int limit) throws QueryExecutionException {
        String fieldAggName = "countByField";

        String val = field == ResponseField.YEAR ? field.value : field.value + ".keyword"; // TODO:!!! Temp solution, text fields need to have keyword at the end

        TermsAggregationBuilder termsAgg = AggregationBuilders.terms(fieldAggName).field(val).size(limit);

        List<String> indexesString = new ArrayList<>(); // TODO: change
        indexes.forEach(item -> indexesString.add(AppConstants.ELASTIC_INDEX_PREFIX + item.value));

        Aggregations agg = elasticRunner.runAggregation(indexesString, termsAgg);

        Terms t = agg.get(fieldAggName);

        List<Pair<Object, Long>> data = new ArrayList<>();
        for (Terms.Bucket bucket : t.getBuckets()) {
            data.add(new Pair<>(bucket.getKey(), bucket.getDocCount()));
        }

        return data;
    }

    @Override
    // TODO: Implement
    public List<Pair<String, Integer>> activePeople(DataSource collectionName, String type, int limit) {
        return null;
    }

    /**
     * Constructs a filter aggregation which creates two filters, one for filtering by owners name,
     * second one for filtering by category.
     * Third term aggregation groups the filtered results by year.
     *
     * {@inheritDoc}
     */
    @Override
    public List<Pair<Long, Long>> patentOwnershipEvolution(List<DataSource> indexes, String owner, String category) throws QueryExecutionException {
        String globalAggName = "patentOwnershipEvolution";
        String ownersAggName = "owners";
        String yearsAggName = "years";

        AggregationBuilder aggregation = AggregationBuilders.global(globalAggName);

        FiltersAggregationBuilder filterAggregationBuilder = AggregationBuilders.filters(ownersAggName,
                new FiltersAggregator.KeyedFilter("owners", QueryBuilders.matchQuery(ResponseField.OWNERS_NAME.value, owner)),
                new FiltersAggregator.KeyedFilter("categories", QueryBuilders.simpleQueryStringQuery(category)));
        aggregation.subAggregation(filterAggregationBuilder);

        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms(yearsAggName).field("year");
        filterAggregationBuilder.subAggregation(termsAggregation);

        List<String> indexesString = new ArrayList<>(); // TODO: !!! change
        indexes.forEach(item -> indexesString.add(AppConstants.ELASTIC_INDEX_PREFIX + item.value));

        Aggregations agg = elasticRunner.runAggregation(indexesString, aggregation);

        Global g = agg.get(globalAggName);
        Filters f = g.getAggregations().get(ownersAggName);
        Terms t = f.getBuckets().get(0).getAggregations().get(yearsAggName);

        List<Pair<Long, Long>> years = new ArrayList<>();
        for (Terms.Bucket bucket : t.getBuckets()) {
            years.add(new Pair<>((Long) bucket.getKey(), bucket.getDocCount()));
        }

        return years;
    }

    @Override
    public List<Pair<Long, Long>> dateHistogram(List<DataSource> indexes) throws QueryExecutionException {
        String dateName = "date_histogram";
        DateHistogramAggregationBuilder dateHistogramAgg = AggregationBuilders.dateHistogram(dateName)
                .calendarInterval(DateHistogramInterval.YEAR)
                .field(ResponseField.DATE.value).keyed(true);


        List<String> indexesString = new ArrayList<>(); // TODO: !!! change
        indexes.forEach(item -> indexesString.add(AppConstants.ELASTIC_INDEX_PREFIX + item.value));
        Aggregations agg = elasticRunner.runAggregation(indexesString, dateHistogramAgg);

        List<Pair<Long, Long>> dates = new ArrayList<>();
        Histogram h = agg.get(dateName);
        for (Histogram.Bucket entry : h.getBuckets()) {
            ZonedDateTime key = (ZonedDateTime) entry.getKey();
            dates.add(new Pair<>((long) key.getYear(), entry.getDocCount()));
        }

        return dates;
    }
}
