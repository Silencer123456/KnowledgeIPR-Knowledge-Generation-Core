package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dataaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dataaccess.ResponseField;

import java.util.List;

/**
 * Abstract interface specifying all the specific queries that can be run on the target database.
 * The concrete implementations of this class are responsible for creation and execution of the queries.
 */
public interface IQueryRunner {

    /**
     * Creates a query which runs an count aggregation on the specified simple field.
     *
     * @param collectionName - Name of the collection on which to run the query
     * @param field          - Field on which to perform the count aggregation
     * @return - Graph data as a list of 'String, Integer' pairs
     */
    List<Pair<String, Integer>> countByField(DataSourceType collectionName, ResponseField field);

    /**
     * Creates a query which runs an count aggregation on the specified field. in this case, the field is an array
     * containing a list of values. E.g. keywords, fos...
     *
     * @param collectionName - Name of the collection on which to run the query
     * @param field - Field on which to perform the count aggregation
     * @return - Graph data as a list of 'String, Integer' pairs
     */
    List<Pair<String, Integer>> countByArrayField(DataSourceType collectionName, ResponseField field);

    /**
     * Queries Mongo database for most active authors.
     * Query selects 20 most active authors along with the number of their publications/patents
     * and sorts them in descending order.
     *
     * db.patent.aggregate([
     * {$project: { _id: 0, "authors.name": 1 } },
     * {$unwind: "$authors" },
     * {$group: { _id: { $toLower: "$authors.name" }, count: { $sum: 1 } }},
     * {$project: { _id: 0,"authors.name": "$_id", count: 1 } },
     * {$sort: { count: -1 } }
     * ], { allowDiskUse: true })
     *
     * @param collectionName - Collection in which to search
     * @param type           - 'authors' or 'owners'
     * @return - List of 'author name, count' pairs
     */
    List<Pair<String, Integer>> activePeople(DataSourceType collectionName, String type, int limit);

    /**
     * Creates a query returning evolution of patents ownership by a specified company (owner)
     * in a specified category.
     *
     * @param collectionName - Name of the collection on which the query should be runAggregation
     * @param owner          - The owner of the patents to search for
     * @param category       - Category, which should be used as filter
     * @return Chart data
     */
    // TODO: maybe generify, replace category string
    List<Pair<Integer, Integer>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category);
}
