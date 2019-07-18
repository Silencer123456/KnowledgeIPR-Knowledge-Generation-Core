package kiv.zcu.knowledgeipr.core.query.queries;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.mongo.IQueryRunner;
import kiv.zcu.knowledgeipr.core.query.ChartQuery;

import java.util.List;

/**
 * Manages the creation of a query returning evolution of patents ownership by a specified company (owner)
 * in a specified category
 */
public class ActiveOwnersQuery extends ChartQuery<String, Integer> {

    private static final String X_AXIS = "Owner's name";
    private static final String Y_AXIS = "Count";

    private IQueryRunner queryCreator;

    private String personType;

    /**
     * todo: Change string parameters to single hashmap with all the values
     *
     * @param queryCreator - The implementation of the query creator, responsible for creating and executing the query
     * @param type         - Type of person (authors or owners)
     */
    public ActiveOwnersQuery(IQueryRunner queryCreator, String type) {
        super("Patent ownership for owner.", X_AXIS, Y_AXIS);

        this.queryCreator = queryCreator;
        this.personType = type;
    }

    @Override
    public List<Pair<String, Integer>> get() {
        return queryCreator.activePeople(DataSourceType.PATENT, personType, 1000);
    }
}
