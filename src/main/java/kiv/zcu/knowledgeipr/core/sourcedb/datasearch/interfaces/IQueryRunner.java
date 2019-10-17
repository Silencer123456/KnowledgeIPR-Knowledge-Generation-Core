package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSource;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;

import java.util.List;

/**
 * Abstract interface specifying all the specific queries that can be run on the target database.
 * The concrete implementations of this class are responsible for creation and execution of the queries.
 */
public interface IQueryRunner {

    /**
     * Creates a search which runs an count aggregation on the specified simple field.
     *
     * @param indexes - Name of the indexes on which to run the search
     * @param field          - Field on which to perform the count aggregation
     * @return - Graph data as a list of 'String, Integer' pairs
     */
    List<Pair<Object, Long>> countByField(List<DataSource> indexes, ResponseField field)
            throws QueryExecutionException;

    /**
     * Creates a search which runs an count aggregation on the specified field. in this case, the field is an array
     * containing a list of values. E.g. keywords, fos...
     *
     * @param indexes - Name of the indexes on which to run the search
     * @param field - Field on which to perform the count aggregation
     * @return - Graph data as a list of 'String, Integer' pairs
     */
    List<Pair<Object, Long>> countByStringArrayField(List<DataSource> indexes, ResponseField field)
            throws QueryExecutionException;

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
    List<Pair<String, Long>> activePeople(DataSource inde, String type, int limit);

    /**
     * Creates a search returning evolution of patents ownership by a specified company (owner)
     * in a specified category.
     *
     * @param indexes - Name of the collection on which the search should be runAggregation
     * @param owner          - The owner of the patents to search for
     * @param category       - Category, which should be used as filter
     * @return Chart data
     */
    // TODO: maybe generify, replace category string
    List<Pair<Long, Long>> patentOwnershipEvolution(List<DataSource> indexes, String owner, String category)
            throws QueryExecutionException;

    List<Pair<Long, Long>> dateHistogram(List<DataSource> indexes)
            throws QueryExecutionException;
}
