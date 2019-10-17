package kiv.zcu.knowledgeipr.core.model.search.aggqueries;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.model.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSource;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;

import java.util.List;

public class DateHistogramAggregation extends ChartQuery<Long, Long> {

    private static final int LIMIT = 1000;

    private static final String Y_AXIS = "Docs count";

    public DateHistogramAggregation(IQueryRunner queryRunner, List<DataSource> indexes) {
        super(queryRunner, "Yearly date histogram for index(es): `" + indexes + "`", "Year", Y_AXIS, indexes);
    }

    @Override
    public List<Pair<Long, Long>> get() throws QueryExecutionException {
        return queryCreator.dateHistogram(indexes);
    }
}
