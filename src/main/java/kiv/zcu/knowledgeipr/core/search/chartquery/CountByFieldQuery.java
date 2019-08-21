package kiv.zcu.knowledgeipr.core.search.chartquery;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dataaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dataaccess.ResponseField;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.IQueryRunner;
import kiv.zcu.knowledgeipr.core.search.ChartQuery;

import java.util.List;

public class CountByFieldQuery extends ChartQuery<String, Integer> {

    private static final int LIMIT = 1000;

    private static final String Y_AXIS = "Count";

    private DataSourceType collection;
    private ResponseField field;

    public CountByFieldQuery(IQueryRunner queryRunner, ResponseField field, DataSourceType collection) {
        super(queryRunner, "Count by " + field.value, field.value + " name", Y_AXIS);

        this.collection = collection;
        this.field = field;
    }

    @Override
    public List<Pair<String, Integer>> get() {
        return queryCreator.countByField(collection, field);
    }
}