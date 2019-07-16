package kiv.zcu.knowledgeipr.core.query.queries;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.IChartQuery;
import kiv.zcu.knowledgeipr.core.dbaccess.IQueryCreator;

import java.util.List;

/**
 * Manages the creation of a query returning evolution of patents ownership by a specified company
 * in a specified category
 */
public class PatentOwnershipEvolutionQuery implements IChartQuery<String, Integer> {

    public static final String NAME = "Patent ownership evolution";
    public static final String X_AXIS = "Year";
    public static final String Y_AXIS = "Count";

    private IQueryCreator queryCreator;

    private String ownersName;
    private String category;

    /**
     * todo: Change string parameters to single hashmap with all the values
     *
     * @param queryCreator
     * @param ownersName
     * @param category
     */
    public PatentOwnershipEvolutionQuery(IQueryCreator queryCreator, String ownersName, String category) {
        this.queryCreator = queryCreator;
        this.ownersName = ownersName;
        this.category = category;
    }

    @Override
    public List<Pair<String, Integer>> get() {
        return queryCreator.getPatentOwnershipEvolutionQuery(DataSourceType.PATENT, ownersName, category);
    }
}
