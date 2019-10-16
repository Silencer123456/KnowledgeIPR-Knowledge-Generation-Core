package kiv.zcu.knowledgeipr.core.model.search;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;

import java.util.List;
import java.util.logging.Logger;

/**
 * Abstract class representing a specific search, which returns chart data
 *
 * @param <T> - The x axis data type
 * @param <V> - The y axis data type
 */
public abstract class ChartQuery<T, V> {

    protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private String title;
    private String xLabel;
    private String yLabel;

    protected IQueryRunner queryCreator;

    public ChartQuery(IQueryRunner queryCreator, String title, String xLabel, String yLabel) {
        this.queryCreator = queryCreator;
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
    }

    /**
     * Implemented methods create the concrete query and from the
     * returned results is constructed a list of chart data.
     *
     * @return - Chart data in form of list of pair values
     */
    public abstract List<Pair<T, V>> get() throws QueryExecutionException;

    public String getTitle() {
        return title;
    }

    public String getxLabel() {
        return xLabel;
    }

    public String getyLabel() {
        return yLabel;
    }

}
