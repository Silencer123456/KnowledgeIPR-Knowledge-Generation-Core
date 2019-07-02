package kiv.zcu.knowledgeipr.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.core.query.QueryOptions;
import kiv.zcu.knowledgeipr.rest.exception.UserQueryException;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
    /**
     * Default name of the Mongo database, in case the configuration file is not found
     */
    private static final String DEFAULT_DB_NAME = "knowingipr";

    /**
     * Path to the mongo configuration file
     */
    private static final String MONGO_CONFIG_PATH = "mongo-config.cfg";

    private String dbName = DEFAULT_DB_NAME;

    /**
     * Mongo database access instance
     */
    private MongoDatabase database;

    public DataRetriever() {
        setup();
    }

    /**
     * Sets up the connection to the Mongo database
     */
    private void setup() {
        loadConfig();
        LOGGER.info("Connecting to the MongoDB database " + dbName);
        MongoClient mongoClient = new MongoClient();
        database = mongoClient.getDatabase(dbName);
    }

    /**
     * Runs the query on the source database and returns a result set.
     *
     * @param query - knowledgeipr.Query to be run
     * @param page  - Page to return
     * @param limit - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.DbRecord</code> instances.
     */
    public List<DbRecord> runQuery(Query query, int page, final int limit) throws MongoQueryException, UserQueryException, MongoExecutionTimeoutException {
        String sourceType = query.getSourceType();
        isValidSourceType(sourceType);

        BsonDocument filter = new BsonDocument();

        addFilters(query.getFilters(), filter);

        addConditionsFilters(query.getConditions(), filter);

        LOGGER.info("Running query: " + filter.toJson() + ", page: " + page + ", limit: " + limit);

        return filter.isEmpty() ? Collections.emptyList() : doSearch(sourceType, filter, limit, page, query.getOptions());
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
    private void addFilters(Map<String, String> filters, BsonDocument bsonDocument) {
        if (filters == null) return;
        boolean textFilterCreated = false;

        if (filters.containsKey("$text")) {
            Bson tmp = Filters.text(filters.get("$text"));
            appendBsonDoc(bsonDocument, tmp, "$text");
            textFilterCreated = true;
            filters.remove("$text");
        }

        for (Map.Entry<String, String> filterEntry : filters.entrySet()) {
            boolean isFieldValid = ResponseField.isValid(filterEntry.getKey());
            if (!isFieldValid) {
                LOGGER.warning("Field " + filterEntry.getKey() + " is not valid, skipping.");
                continue;
            }
            // TODO: Vyresit pripad duplikace fieldupaTE
            Bson tmp;
            // If the text filter was not specified in the query, we create one from the current field
            if (!textFilterCreated && isKeyTextIndexed(filterEntry.getKey())) {
                tmp = Filters.text(filterEntry.getValue());
                appendBsonDoc(bsonDocument, tmp, "$text");
                textFilterCreated = true;
            }

            Pattern regex = Pattern.compile(filterEntry.getValue(), Pattern.CASE_INSENSITIVE);
            Bson r = Filters.regex(filterEntry.getKey(), regex);
            Bson eq = Filters.eq(filterEntry.getKey(), filterEntry.getValue());
            tmp = Filters.or(eq, r);

            //appendBsonDoc(bsonDocument, tmp, filterEntry.getKey());
            appendBsonDoc(bsonDocument, tmp, "$or");
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

    // TODO: parametrize the MAX time of query; Check if throwing an error closes cursor

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
    private List<DbRecord> doSearch(String collectionName, Bson filter, int limit, int page, QueryOptions options)
            throws MongoQueryException, MongoExecutionTimeoutException {

        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<DbRecord> dbRecords = new ArrayList<>();

        FindIterable<Document> iterable = collection
                .find(filter)
                .skip(page > 0 ? ((page - 1) * limit) : 0)
                .limit(limit)
                .projection(getProjectionFields())
                .maxTime(options.getTimeout(), TimeUnit.SECONDS);
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
    private Bson getProjectionFields() {
        return fields(
                include(
                        //Projections.metaTextScore("score"),
                        ResponseField.TITLE.toString(),
                        ResponseField.YEAR.toString(),
                        ResponseField.ABSTRACT.toString(),
                        ResponseField.AUTHORS.toString(),
                        ResponseField.OWNERS.toString(),
                        ResponseField.DOCUMENT_ID.toString(),
                        ResponseField.PUBLISHER.toString(),
                        ResponseField.DATA_SOURCE.toString()));
    }

    /**
     * Loads configuration of the MongoDB connection
     */
    private void loadConfig() {
        Properties prop = new Properties();
        try {
            InputStream inputStream = getClass()
                    .getClassLoader().getResourceAsStream(MONGO_CONFIG_PATH);
            if (inputStream != null) {
                prop.load(inputStream);
                dbName = prop.getProperty("db_name");
                LOGGER.info("MongoDB configuration file loaded.");
            } else {
                LOGGER.severe("The MongoDB configuration file not found. Setting database to default: " + dbName);
            }
        } catch (IOException e) {
            LOGGER.severe("Unable to find " + MONGO_CONFIG_PATH + " file. Using default " +
                    "database: " + dbName);
            e.printStackTrace();
        }
    }
}

