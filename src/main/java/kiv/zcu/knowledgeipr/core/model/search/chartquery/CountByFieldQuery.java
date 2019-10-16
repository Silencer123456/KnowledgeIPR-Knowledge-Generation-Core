package kiv.zcu.knowledgeipr.core.model.search.chartquery;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.model.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;

import java.util.List;

public class CountByFieldQuery extends ChartQuery<String, Long> {

    private static final int LIMIT = 1000;

    private static final String Y_AXIS = "Count";

    private DataSourceType collection;
    private ResponseField field;

    public CountByFieldQuery(IQueryRunner queryRunner, ResponseField field, DataSourceType collection) {
        super(queryRunner, "Count aggregation of " + field.value, "Term: " + field.value, Y_AXIS);

        this.collection = collection;
        this.field = field;
    }

    @Override
    public List<Pair<String, Long>> get() throws QueryExecutionException {
        return queryCreator.countByField(collection, field);
    }
}
