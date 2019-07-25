package kiv.zcu.knowledgeipr.core.query.queries;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;
import kiv.zcu.knowledgeipr.core.dbaccess.mongo.IQueryRunner;
import kiv.zcu.knowledgeipr.core.query.ChartQuery;

import java.util.List;

public class CountByArrayFieldQuery extends ChartQuery<String, Integer> {
    private static final int LIMIT = 1000;

    private static final String Y_AXIS = "Count";

    private DataSourceType collection;
    private ResponseField field;

    public CountByArrayFieldQuery(IQueryRunner queryRunner, ResponseField field, DataSourceType collection) {
        super(queryRunner, "Count by " + field.value, field.value + " name", Y_AXIS);

        this.collection = collection;
        this.field = field;
    }

    @Override
    public List<Pair<String, Integer>> get() {
        return queryCreator.countByArrayField(collection, field);
    }
}
