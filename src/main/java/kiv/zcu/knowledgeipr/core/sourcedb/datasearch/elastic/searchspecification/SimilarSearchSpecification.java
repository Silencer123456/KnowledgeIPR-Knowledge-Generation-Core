package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.searchspecification;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.utils.AppConstants;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class SimilarSearchSpecification extends SearchSpecification<Search> {
    private String id;

    public SimilarSearchSpecification(Search search, final String id) {
        super(search);
        this.id = id;
    }

    @Override
    public QueryBuilder get() {
        return QueryBuilders.moreLikeThisQuery(new String[]{"title"}, null,
                new MoreLikeThisQueryBuilder.Item[]{new MoreLikeThisQueryBuilder.Item(AppConstants.ELASTIC_INDEX_PREFIX + search.getDataSourceType().value, id)})
                .minTermFreq(1)
                .minDocFreq(1);
    }
}
