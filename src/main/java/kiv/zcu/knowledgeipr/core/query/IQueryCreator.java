package kiv.zcu.knowledgeipr.core.query;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;

import java.util.List;

/**
 * Abstract interface specifying all the specific queries that can be run on the database.
 * The concrete implementations of this class are responsible for creation and execution of the queries.
 */
public interface IQueryCreator {

    List<Pair<String, Integer>> countByField(String collectionName, ResponseField field);

    List<Pair<String, Integer>> activePeople(String collectionName, String type);

    /**
     * // TODO: maybe generify, replace category string
     *
     * @param collectionName - Name of the collection on which the query should be run
     * @param owner          - The owner of the patents to search for
     * @param category       - Category, which should be used as filter
     * @return Chart data
     */
    List<Pair<String, Integer>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category);
}
