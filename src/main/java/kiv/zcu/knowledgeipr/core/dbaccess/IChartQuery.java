package kiv.zcu.knowledgeipr.core.dbaccess;

import javafx.util.Pair;

import java.util.List;

public interface IChartQuery<T, V> {
    List<Pair<T, V>> get();
}
