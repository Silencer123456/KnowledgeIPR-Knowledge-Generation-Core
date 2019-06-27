package kiv.zcu.knowledgeipr.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoQueryException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import kiv.zcu.knowledgeipr.rest.exception.UserQueryException;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Accesses the source database, runs queries on it and gets
 * a results set back.
 */
public class DataRetriever {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String MONGO_CONFIG_PATH = "mongo-config.cfg";

    private String dbName = "diploma";

    MongoDatabase database;

    public DataRetriever() {
        setup();
    }

    private void setup() {
        loadConfig();
        LOGGER.info("Connecting to the MongoDB database " + dbName);
        MongoClient mongoClient = new MongoClient();
        database = mongoClient.getDatabase(dbName);
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

    /**
     * Runs the query on the source database and returns a result set.
     *
     * @param query - knowledgeipr.Query to be run
     * @param page - Page to return
     * @param limit - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.DbRecord</code> instances.
     */
    public List<DbRecord> runQuery(Query query, int page, final int limit) throws MongoQueryException, UserQueryException {
        LOGGER.info("Running query " + query.getTextQuery() + ", page: " + page + ", limit: " + limit);
        String sourceType = query.getSourceType();
        if (!sourceType.equals("patent") && !sourceType.equals("publication")) {
            throw new UserQueryException("Unknown data source type: " + sourceType + ". Only 'patent' and 'publication' allowed");
        }

        //sourceType = "test";

        Map<String, String> filters = query.getFilters();

        Bson filter = Filters.and(); // Start with empty and, and append eq in the loop
        BsonDocument bsonDocument = filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
        for (Map.Entry<String, String> filterEntry : filters.entrySet()) {
            if (!ResponseField.isValid(filterEntry.getKey())) {
                LOGGER.warning("Field " + filterEntry.getKey() + "is not valid, skipping.");
                continue;
            }
            Bson tmp;
            if (filterEntry.getKey().equals("text")) {
                tmp = Filters.text(filterEntry.getValue());
            } else {
                tmp = Filters.eq(filterEntry.getKey(), filterEntry.getValue());
            }

            BsonDocument docToAppend = tmp.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
            bsonDocument.append(filterEntry.getKey(), docToAppend.get(filterEntry.getKey()));
        }

        LOGGER.info("Running query: " + bsonDocument);

        List<DbRecord> resultRecords = doTextSearch(sourceType, bsonDocument, limit, page);



//        Bson tmp;
//
//        for (Map.Entry<String, String> filterEntry : query.getFilters().entrySet()) {
//            tmp = Filters.eq(filterEntry.getKey(), filterEntry.getValue());
//        }
//
//        Bson filter = Filters.and(
//                Filters.text(query.getTextQuery()),
//                Filters.eq("test", "hodnota"),
//                Filters.eq("test2", "hodnota2")
//                );

//        if (query.isSelectiveSearch()) {
//            LOGGER.info("Running selective search on field " + query.getFilter());
//            //Pattern regex = Pattern.compile(query.getTextQuery(), Pattern.CASE_INSENSITIVE); // Regex is very slow
//            //filter = Filters.eq(query.getFilter(), regex);
//
//            LOGGER.info("Running regular search");
//            resultRecords = doRegularSearch(sourceType, query.getFilter(), query.getTextQuery());
//
//            /*if (resultRecords.isEmpty()) {
//                LOGGER.info("No results found by regular seach, trying text and regex search");
//                Pattern regex = Pattern.compile(query.getTextQuery(), Pattern.CASE_INSENSITIVE);
//                Bson filter = Filters.and(Filters.text(query.getTextQuery()), Filters.eq(query.getFilter(), regex));
//                resultRecords = doTextSearch(sourceType, filter, limit, page);
//            }*/
//
//        } else {
//            LOGGER.info("Running non selective search. Using text index");
//            Bson filter = Filters.text(query.getTextQuery());
//            resultRecords = doTextSearch(sourceType, filter, limit, page);
//        }

        return resultRecords;
    }

    private List<DbRecord> doRegularSearch(String collectionName, String fieldName, String value) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<DbRecord> dbRecords = new ArrayList<>();

        MongoCursor<Document> cursor = collection.find(eq(fieldName, value))
                .projection(getProjectionFields())
                .iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            dbRecords.add(new DbRecord(document));
        }

        return dbRecords;
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
    private List<DbRecord> doTextSearch(String collectionName, Bson filter, int limit, int page) throws MongoQueryException {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<DbRecord> dbRecords = new ArrayList<>();

        MongoCursor<Document> cursor;
        cursor = collection
                .find(filter)
                .skip(page > 0 ? ((page-1) * limit) : 0)
                .limit(limit)
                .projection(getProjectionFields())
                //.sort(Sorts.metaTextScore("score"))
                .iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            dbRecords.add(new DbRecord(document));
        }

        cursor.close();

        return dbRecords;
    }

    /**
     * Returns a list of fields to be projected in the Mongo documents
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
}

