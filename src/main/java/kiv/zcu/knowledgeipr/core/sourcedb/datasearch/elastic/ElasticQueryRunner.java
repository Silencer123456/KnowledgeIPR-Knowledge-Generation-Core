package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import javafx.util.Pair;
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

public class ElasticQueryRunner implements IQueryRunner {

    private CommonElasticRunner elasticRunner;

    public ElasticQueryRunner() {
        elasticRunner = CommonElasticRunner.getInstance();
    }

    @Override
    public List<Pair<String, Integer>> countByField(DataSourceType collectionName, ResponseField field) {
        return null;
    }

    @Override
    public List<Pair<String, Integer>> countByArrayField(DataSourceType collectionName, ResponseField field) {
        return null;
    }

    @Override
    public List<Pair<String, Integer>> activePeople(DataSourceType collectionName, String type, int limit) {
        return null;
    }

    @Override
    public List<Pair<Long, Long>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category) {
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
