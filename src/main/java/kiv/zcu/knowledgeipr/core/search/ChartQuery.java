package kiv.zcu.knowledgeipr.core.search;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.IQueryRunner;

import java.util.List;

/**
 * Abstract class representing a specific search, which returns chart data
 *
 * @param <T> - The x axis data type
 * @param <V> - The y axis data type
 */
public abstract class ChartQuery<T, V> {
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
     * Creates the search and gets the results
     * @return - Chart data in form of list of pair values
     */
    public abstract List<Pair<T, V>> get();

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
