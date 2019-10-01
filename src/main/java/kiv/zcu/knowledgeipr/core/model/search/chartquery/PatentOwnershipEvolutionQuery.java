package kiv.zcu.knowledgeipr.core.model.search.chartquery;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.model.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;

import java.util.List;

/**
 * Manages the creation of a search returning evolution of patents ownership by a specified company (owner)
 * in a specified category
 */
public class PatentOwnershipEvolutionQuery extends ChartQuery<Long, Long> {

    private static final String X_AXIS = "Year";
    private static final String Y_AXIS = "Count";

    private String ownersName;
    private String category;

    /**
     * @param queryCreator - The implementation of the search creator, responsible for creating and executing the search
     * @param ownersName - Name of the owner to search
     * @param category - Name of the category to search
     */
    public PatentOwnershipEvolutionQuery(IQueryRunner queryCreator, String ownersName, String category) {
        super(queryCreator, "Patent ownership for owner `" + ownersName + "` in `" + category + "` category.", X_AXIS, Y_AXIS);

        this.queryCreator = queryCreator;
        this.ownersName = ownersName;
        this.category = category;
    }

    @Override
    public List<Pair<Long, Long>> get() {
        LOGGER.info("Running getPatentOwnershipEvolutionQuery search on `" + ownersName + "` owner and `" + category + "` category on " + DataSourceType.PATENT + " collection.");

        return queryCreator.getPatentOwnershipEvolutionQuery(DataSourceType.PATENT, ownersName, category);
    }
}
