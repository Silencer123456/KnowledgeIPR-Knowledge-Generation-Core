package kiv.zcu.knowledgeipr.core.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import kiv.zcu.knowledgeipr.core.DataSourceType;
import kiv.zcu.knowledgeipr.core.ResponseField;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.rest.exception.UserQueryException;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Accesses the source database, runs queries on it and gets
 * a results set back.
 */
public class DataRetriever {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private MongoDatabase database;

    public DataRetriever(MongoConnection mongoConnection) {
        database = mongoConnection.getConnectionInstance();
    }

    /**
     * Runs the query on the source database and returns a result set.
     * First a quick query is run, performing an exact match search, which should be very fast when using index.
     * This query is limited to just few seconds of execution.
     * If no results are returned by that time, the second main query is run with user
     * specified timeout.
     *
     * @param query - knowledgeipr.Query to be run
     * @param page  - Page to return
     * @param limit - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.DbRecord</code> instances.
     */
    public List<DbRecord> runQuery(Query query, int page, final int limit) throws MongoQueryException, UserQueryException, MongoExecutionTimeoutException {
        String sourceType = query.getSourceType();
        isValidSourceType(sourceType);

        BsonDocument filter = addAllFilters(query, false);
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        if (filterContainsIndex(query.getFilters())) {
            LOGGER.info("Running 1. query: " + filter.toJson() + ", page: " + page + ", limit: " + limit);
            try {
                List<DbRecord> results = doSearch(sourceType, filter, limit, page, 10);
                // If something was found, we do not need to run the second query
                if (!results.isEmpty()) {
                    return results;
                }
            } catch (MongoExecutionTimeoutException e) {
                LOGGER.info("Nothing found during quick query");
            }
        }

        filter = addAllFilters(query, true);

        LOGGER.info("Running 2. query: " + filter.toJson() + ", page: " + page + ", limit: " + limit);
        // Run second query
        return doSearch(sourceType, filter, limit, page, query.getOptions().getTimeout());
    }

    /**
     * Adds all filters from the query and creates a bson document from them
     *
     * @param query
     * @return Bson document containing all the filters
     */
    private BsonDocument addAllFilters(Query query, boolean useRegex) {
        BsonDocument filter = new BsonDocument();

        addFilters(query.getFilters(), filter, useRegex);

        addConditionsFilters(query.getConditions(), filter);

        return filter;
    }

    /**
     * Checks if the provided data source is valid or not
     *
     * @param sourceType - Source of data to check
     * @throws UserQueryException if the source type is not valid
     */
    private void isValidSourceType(String sourceType) throws UserQueryException {
        if (!DataSourceType.containsField(sourceType)) {
            throw new UserQueryException("Unknown data source type: " + sourceType + ". Only 'patent' and 'publication' allowed");
        }
    }

    /**
     * Creates Mongo filters according to the specified query
     *
     * @param filters      - list of filters from the query, which will be converted to Mongo filters
     * @param bsonDocument - Bson document, to which add the created filters
     */
    private void addFilters(Map<String, String> filters, BsonDocument bsonDocument, boolean useRegex) {
        if (filters == null) return;
        boolean textFilterCreated = false;

        if (filters.containsKey("$text")) {
            Bson tmp = Filters.text(filters.get("$text"));
            appendBsonDoc(bsonDocument, tmp, "$text");
            textFilterCreated = true;
        }

        for (Map.Entry<String, String> filterEntry : filters.entrySet()) {
            boolean isFieldValid = ResponseField.isValid(filterEntry.getKey());
            if (!isFieldValid) {
                LOGGER.warning("Field " + filterEntry.getKey() + " is not valid, skipping.");
                continue;
            }

            Bson tmp;
            // If the text filter was not specified in the query, we create one from the current field
            if (!textFilterCreated && isKeyTextIndexed(filterEntry.getKey())) {
                tmp = Filters.text(filterEntry.getValue());
                appendBsonDoc(bsonDocument, tmp, "$text");
                textFilterCreated = true;
            }

            if (useRegex) {
                Pattern regex = Pattern.compile(filterEntry.getValue(), Pattern.CASE_INSENSITIVE);
                tmp = Filters.regex(filterEntry.getKey(), regex);
            } else {
                tmp = Filters.eq(filterEntry.getKey(), filterEntry.getValue());
            }

//            Pattern regex = Pattern.compile(filterEntry.getValue(), Pattern.CASE_INSENSITIVE);
//            Bson r = Filters.regex(filterEntry.getKey(), regex);
//            Bson eq = Filters.eq(filterEntry.getKey(), filterEntry.getValue());
//            tmp = Filters.or(eq, r);

            appendBsonDoc(bsonDocument, tmp, filterEntry.getKey());
            //appendBsonDoc(bsonDocument, tmp, "$or");
        }
    }

    /**
     * Returns true if the key is part of the text index
     * TODO: Dynamically read from Mongo
     *
     * @param key - Key to search
     * @return true if the key is part of text index, false otherwise
     */
    private boolean isKeyTextIndexed(String key) {
        //TODO: get info directly from Mongo
        return key.equals("title") ||
                key.equals("authors.name") ||
                key.equals("owners.name") ||
                key.equals("abstract");
    }
    // TODO: Dynamically read from Mongo

    /**
     * Checks if the provided filters map contains field, which is a part of
     * an index in Mongo
     *
     * @param filters
     * @return
     */
    private boolean filterContainsIndex(Map<String, String> filters) {
        return filters.containsKey(ResponseField.DOCUMENT_ID.value) ||
                filters.containsKey(ResponseField.TITLE.value);
    }

    /**
     * Appends bson to input Bson document
     *
     * @param bsonDocument - Bson document to which we want to append
     * @param bson         - bson which we want to append to the document
     * @param key          - key that we want to add to the bson document
     */
    private void appendBsonDoc(BsonDocument bsonDocument, Bson bson, String key) {
        BsonDocument docToAppend = bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());

        if (docToAppend.get(key) == null) return;
        bsonDocument.append(key, docToAppend.get(key));
    }

    /**
     * Creates condition filters from the query.
     * Like gt, lt
     *
     * @param conditions   - List of conditions to be converted to the mongo filters
     * @param bsonDocument - Bson document, to which add the option filters
     */
    private void addConditionsFilters(Map<String, Map<String, Integer>> conditions, BsonDocument bsonDocument) {
        if (conditions == null) return;

        for (Map.Entry<String, Map<String, Integer>> optionEntry : conditions.entrySet()) {
            if (optionEntry.getValue() == null) continue;

            Bson tmp;
            if (optionEntry.getValue().containsKey("$gt")) {
                tmp = Filters.gt(optionEntry.getKey(), optionEntry.getValue().get("$gt"));
            } else if (optionEntry.getValue().containsKey("$lt")) {
                tmp = Filters.lt(optionEntry.getKey(), optionEntry.getValue().get("$lt"));
            } else if (optionEntry.getValue().containsKey("$eq")) {
                tmp = Filters.eq(optionEntry.getKey(), optionEntry.getValue().get("$eq"));
            } else {
                tmp = Filters.and();
            }

            appendBsonDoc(bsonDocument, tmp, optionEntry.getKey());
        }
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
    private List<DbRecord> doSearch(String collectionName, Bson filter, int limit, int page, int timeout)
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

