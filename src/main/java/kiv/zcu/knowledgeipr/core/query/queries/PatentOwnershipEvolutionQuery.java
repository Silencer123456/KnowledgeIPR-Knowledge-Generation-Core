package kiv.zcu.knowledgeipr.core.query.queries;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.query.ChartQuery;
import kiv.zcu.knowledgeipr.core.query.IQueryCreator;

import java.util.List;

/**
 * Manages the creation of a query returning evolution of patents ownership by a specified company (owner)
 * in a specified category
 */
public class PatentOwnershipEvolutionQuery extends ChartQuery<String, Integer> {

    private static final String X_AXIS = "Year";
    private static final String Y_AXIS = "Count";

    private IQueryCreator queryCreator;

    private String ownersName;
    private String category;

    /**
     * todo: Change string parameters to single hashmap with all the values
     *
     * @param queryCreator - The implementation of the query creator, responsible for creating and executing the query
     * @param ownersName - Name of the owner to search
     * @param category - Name of the category to search
     */
    public PatentOwnershipEvolutionQuery(IQueryCreator queryCreator, String ownersName, String category) {
        super("Patent ownership for owner " + ownersName + " in " + category + " category.", X_AXIS, Y_AXIS);

        this.queryCreator = queryCreator;
        this.ownersName = ownersName;
        this.category = category;
    }

    @Override
    public List<Pair<String, Integer>> get() {
        return queryCreator.getPatentOwnershipEvolutionQuery(DataSourceType.PATENT, ownersName, category);
    }
}
