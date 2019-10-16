package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
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
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

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
    public List<Pair<String, Long>> countByField(DataSourceType collectionName, ResponseField field) throws QueryExecutionException {
        return countByStringArrayField(collectionName, field);
    }

    @Override
    public List<Pair<String, Long>> countByStringArrayField(DataSourceType collectionName, ResponseField field) throws QueryExecutionException {
        int limit = 25;
        String fieldAggName = "countByField";

        TermsAggregationBuilder termsAgg = AggregationBuilders.terms(fieldAggName).field(field.value + ".keyword").size(limit);

        Aggregations agg = elasticRunner.runAggregation(AppConstants.ELASTIC_INDEX_PREFIX + collectionName.value, termsAgg);
        if (agg == null) {
            throw new QueryExecutionException("Aggregation " + termsAgg.toString() + " failed.");
        }

        Terms t = agg.get(fieldAggName);

        List<Pair<String, Long>> data = new ArrayList<>();
        for (Terms.Bucket bucket : t.getBuckets()) {
            data.add(new Pair<>((String) bucket.getKey(), bucket.getDocCount()));
        }

        return data;
    }

    @Override
    public List<Pair<String, Long>> activePeople(DataSourceType collectionName, String type, int limit) {
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
    public List<Pair<Long, Long>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category) throws QueryExecutionException {
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

        Aggregations agg = elasticRunner.runAggregation(AppConstants.ELASTIC_INDEX_PREFIX + collectionName.value, aggregation);
        if (agg == null) {
            throw new QueryExecutionException("Aggregation " + aggregation.toString() + " failed.");
        }

        Global g = agg.get(globalAggName);
        Filters f = g.getAggregations().get(ownersAggName);
        Terms t = f.getBuckets().get(0).getAggregations().get(yearsAggName);

        List<Pair<Long, Long>> years = new ArrayList<>();
        for (Terms.Bucket bucket : t.getBuckets()) {
            years.add(new Pair<>((Long) bucket.getKey(), bucket.getDocCount()));
        }

        return years;
    }
}
