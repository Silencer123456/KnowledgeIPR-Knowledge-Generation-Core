package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.dbconnection.MongoConnection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * A singleton class running common Mongo queries.
 *
 * @author Stepan Baratta
 * created on 7/10/2019
 */
public class CommonMongoRunner {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static CommonMongoRunner instance;

    private MongoDatabase database;

    public CommonMongoRunner() {
        database = MongoConnection.getInstance().getConnectionInstance();
    }

    /**
     * Performs the actual execution of the search
     *
     * @param collectionName - Name of the collection on which to perform the search
     * @param list           - List of Bson documents with the search
     * @return - Iterator of retrieved documents
     */
    AggregateIterable<Document> runAggregation(DataSourceType collectionName, List<Bson> list) {
        LOGGER.info("MongoDB search: " + Arrays.toString(list.toArray()));
        MongoCollection<Document> collection = database.getCollection(collectionName.value);
        return collection.aggregate(list).allowDiskUse(true);
    }

    /**
     * Runs a search performing a sum accumulation
     * This method should be used if the sum is performed on a list of arrays,
     * an unwind operation is performed
     *
     * @param collectionName - Collection name to be queried
     * @param arrayName      - Name of the array to unwind
     * @param fieldName      - The name of the field to sum
     * @param limit          - Number of returned results
     * @return - Iterator with results
     */
    AggregateIterable<Document> runCountUnwindAggregation(DataSourceType collectionName, String arrayName, String fieldName, int limit) {
        List<Bson> list = Arrays.asList(
                project(new Document("_id", 0)
                        .append(fieldName, 1)),
                unwind("$" + arrayName),
                //group(new Document("$toLower", "$" + fieldName),
                group("$" + fieldName, Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append(fieldName, "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(limit));

        return runAggregation(collectionName, list);
    }

    /**
     * Runs a search performing a sum accumulation.
     * From the <code>runCountUnwindAggregation</code> method, this method should
     * be used on a regular field without an array
     *
     * @param collectionName - Collection name to be queried
     * @param fieldName      - The name of the field to sum
     * @param limit          - Number of returned results
     * @return - Iterator with results
     */
    AggregateIterable<Document> runCountAggregation(DataSourceType collectionName, String fieldName, int limit) {
        List<Bson> list = Arrays.asList(
                project(new Document("_id", 0)
                        .append(fieldName, 1)),
                group("$" + fieldName, Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append(fieldName, "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(limit));

        return runAggregation(collectionName, list);
    }

    /**
     * Runs a search on a Mongo collection on the text index.
     * Returns a list of results limited by the specified limit.
     * The returned result list contains only projected fields relevant to the response.
     * If nothing is found, an empty list is returned
     *
     * TODO: Solve performance while sorting by meta text score
     * @param filter         - The search parameter filter
     * @param collectionName - Name of the Mongo collection in which to runAggregation the search
     * @param limit          - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.MongoRecord</code> instances.
     */
    List<MongoRecord> doSearch(DataSourceType collectionName, Bson filter, int limit, int page, int timeout)
            throws MongoQueryException, MongoExecutionTimeoutException {

        MongoCollection<Document> collection = database.getCollection(collectionName.value);
        List<MongoRecord> mongoRecords = new ArrayList<>();

        FindIterable<Document> iterable = collection
                .find(filter)
                .skip(page > 0 ? ((page - 1) * limit) : 0)
                .projection(getProjectionFields(collectionName.value))
                //.sort(Sorts.metaTextScore("score"))
                .limit(limit)
                .maxTime(timeout, TimeUnit.SECONDS)
                .collation(Collation.builder().locale("en").collationStrength(CollationStrength.SECONDARY).build());
        try (MongoCursor<Document> cursor = iterable.iterator()) { // Automatically closes the cursor
            while (cursor.hasNext()) {
                Document document = cursor.next();
                mongoRecords.add(new MongoRecord(document));
            }
        }

        return mongoRecords;
    }

    /**
     * Returns a list of fields to be projected in the Mongo documents
     *
     * @return - BSON representation of projected fields
     */
    private Bson getProjectionFields(String collectionName) {
        if (collectionName.equals(DataSourceType.PATENT.value)) {
            return fields(
                    //exclude("_id"),
                    //Projections.metaTextScore("score"),
                    include(
                            ResponseField.TITLE.toString(),
                            ResponseField.YEAR.toString(),
                            ResponseField.DATE.toString(),
                            ResponseField.ABSTRACT.toString(),
                            ResponseField.AUTHORS.toString(),
                            ResponseField.OWNERS.toString(),
                            ResponseField.DOCUMENT_ID.toString(),
                            ResponseField.DATA_SOURCE.toString(),
                            ResponseField.LANG.toString(),
                            ResponseField.STATUS.toString(),
                            ResponseField.COUNTRY.toString()
                    ));
        } else {
            return fields(
                    //exclude("_id"),
                    include(
                            //Projections.metaTextScore("score"),
                            ResponseField.TITLE.toString(),
                            ResponseField.YEAR.toString(),
                            ResponseField.ABSTRACT.toString(),
                            ResponseField.AUTHORS.toString(),
                            ResponseField.PUBLISHER.toString(),
                            ResponseField.DATA_SOURCE.toString(),
                            ResponseField.FOS.toString(),
                            ResponseField.ISSUE.toString(),
                            ResponseField.URL.toString(),
                            ResponseField.KEYWORDS.toString(),
                            ResponseField.VENUE.toString(),
                            ResponseField.LANG.toString()
                    ));
        }
    }

    /**
     * Returns the single instance of this object. If the instance is not yet constructed, it is created.
     *
     * @return single instance
     */
    public static CommonMongoRunner getInstance() {
        if (instance == null) {
            instance = new CommonMongoRunner();
        }

        return instance;
    }
}
