package kiv.zcu.knowledgeipr.core.mongo;

import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import kiv.zcu.knowledgeipr.core.DataSourceType;
import kiv.zcu.knowledgeipr.core.ResponseField;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * @author Stepan Baratta
 * created on 7/10/2019
 */
public class MongoRunner {

    private MongoDatabase database;

    public MongoRunner(MongoConnection connection) {
        database = connection.getConnectionInstance();
    }

    /**
     * Runs a query performing a sum accumulation
     * This method should be used if the sum is performed on a list of arrays,
     * an unwind operation is performed
     *
     * @param collectionName - Collection name to be queried
     * @param arrayName      - Name of the array to unwind
     * @param fieldName      - The name of the field to sum
     * @param limit          - Number of returned results
     * @return - Iterator with results
     */
    public AggregateIterable<Document> runUnwindAggregation(String collectionName, String arrayName, String fieldName, int limit) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        return collection.aggregate(Arrays.asList(
                project(new Document("_id", 0)
                        .append(fieldName, 1)),
                unwind("$" + arrayName),
                group(new Document("$toLower", "$" + fieldName),
                        Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append(fieldName, "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(limit)
        )).allowDiskUse(true);
    }

    /**
     * Runs a query performing a sum accumulation.
     * From the <code>runUnwindAggregation</code> method, this method should
     * be used on a regular field without an array
     *
     * @param collectionName - Collection name to be queried
     * @param fieldName      - The name of the field to sum
     * @param limit          - Number of returned results
     * @return - Iterator with results
     */
    public AggregateIterable<Document> runAggregation(String collectionName, String fieldName, int limit) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        return collection.aggregate(Arrays.asList(
                project(new Document("_id", 0)
                        .append(fieldName, 1)),
                group("$" + fieldName, Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append(fieldName, "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(limit)
        )).allowDiskUse(true);
    }

    /**
     * Runs a query on a Mongo collection on the text index.
     * Returns a list of results limited by the specified limit.
     * The returned result list contains only projected fields relevant to the response.
     *
     * @param filter         - The search parameter filter
     * @param collectionName - Name of the Mongo collection in which to run the search
     * @param limit          - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.DbRecord</code> instances.
     */
    public List<DbRecord> doSearch(String collectionName, Bson filter, int limit, int page, int timeout)
            throws MongoQueryException, MongoExecutionTimeoutException {

        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<DbRecord> dbRecords = new ArrayList<>();

        FindIterable<Document> iterable = collection
                .find(filter)
                .skip(page > 0 ? ((page - 1) * limit) : 0)
                .limit(limit)
                .projection(getProjectionFields(collectionName))
                .maxTime(timeout, TimeUnit.SECONDS);
        //.sort(Sorts.metaTextScore("score"))

        try (MongoCursor<Document> cursor = iterable.iterator()) { // Automatically closes the cursor
            while (cursor.hasNext()) {
                Document document = cursor.next();
                dbRecords.add(new DbRecord(document));
            }
        }

        return dbRecords;
    }

    /**
     * Returns a list of fields to be projected in the Mongo documents
     *
     * @return - BSON representation of projected fields
     */
    private Bson getProjectionFields(String collectionName) {
        if (collectionName.equals(DataSourceType.PATENT.value)) {
            return fields(
                    include(
                            //Projections.metaTextScore("score"),
                            ResponseField.TITLE.toString(),
                            ResponseField.YEAR.toString(),
                            ResponseField.DATE.toString(),
                            ResponseField.ABSTRACT.toString(),
                            ResponseField.AUTHORS.toString(),
                            ResponseField.OWNERS.toString(),
                            ResponseField.DOCUMENT_ID.toString(),
                            ResponseField.DATA_SOURCE.toString()
                    ));
        } else {
            return fields(
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
}
