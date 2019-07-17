package kiv.zcu.knowledgeipr.core.report;

import javafx.util.Pair;

import java.util.List;

public class Chart<X, Y> {
    private String title;
    private String xLabel;
    private String yLabel;
    private List<Pair<X, Y>> data;

    public Chart(String title, String xLabel, String yLabel, List<Pair<X, Y>> data) {
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.data = data;
    }
}
