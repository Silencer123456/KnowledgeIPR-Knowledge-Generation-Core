package kiv.zcu.knowledgeipr.core.model.search.aggqueries;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.model.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSource;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;

import java.util.List;

// TODO: Implements
public class ListAllAuthorsAggregation extends ChartQuery<Object, Long> {
    private static final int LIMIT = 1000;

    private static final String Y_AXIS = "Count";

    private ResponseField field;

    public ListAllAuthorsAggregation(IQueryRunner queryRunner, ResponseField field, List<DataSource> indexes) {
        super(queryRunner, "Count by " + field.value, field.value + " name", Y_AXIS, indexes);

        this.field = field;
    }

    @Override
    public List<Pair<Object, Long>> get() throws QueryExecutionException {
        return queryCreator.countByStringArrayField(indexes, field, 25);
    }
}
