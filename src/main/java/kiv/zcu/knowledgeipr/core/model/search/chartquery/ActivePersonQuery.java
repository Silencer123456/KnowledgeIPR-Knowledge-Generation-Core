package kiv.zcu.knowledgeipr.core.model.search.chartquery;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.model.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;

import java.util.List;

/**
 * Manages the creation of a search returning evolution of patents ownership or authorship by a specified company (owner)
 * or author in a specified category
 */
public class ActivePersonQuery extends ChartQuery<String, Long> {

    private static final int LIMIT = 1000;

    private static final String X_AXIS = "Owner's name";
    private static final String Y_AXIS = "Count";

    private final String personType;

    /**
     * todo: Change string parameters to single hashmap with all the values
     *
     * @param queryCreator - The implementation of the search creator, responsible for creating and executing the search
     * @param type         - Type of person (authors or owners)
     */
    public ActivePersonQuery(IQueryRunner queryCreator, String type) {
        super(queryCreator, "Patent ownership for " + type, X_AXIS, Y_AXIS);

        this.personType = type;
    }

    @Override
    public List<Pair<String, Long>> get() throws QueryExecutionException {
        return queryCreator.activePeople(DataSourceType.PATENT, personType, LIMIT);
    }
}
