package kiv.zcu.knowledgeipr.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoQueryException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

    private static final String MONGO_CONFIG_PATH = "mongo-config.cfg";

    private String dbName = "diploma";

    MongoDatabase database;

    public DataRetriever() {
        setup();
    }

    private void setup() {
        loadConfig();
        MongoClient mongoClient = new MongoClient();
        database = mongoClient.getDatabase(dbName);
    }

    /**
     * Loads configuration of the MongoDB connection
     */
    private void loadConfig() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(MONGO_CONFIG_PATH));
            dbName = prop.getProperty("db_name");
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
    public List<DbRecord> runQuery(Query query, int page, final int limit) throws MongoQueryException {

        String sourceType = query.getSourceType();

        Bson filter;
        if (query.isSelectiveSearch()) {
            Pattern regex = Pattern.compile(query.getQuery(), Pattern.CASE_INSENSITIVE);
            filter = Filters.eq(query.getFilter(), regex);
        } else {
            filter = Filters.text(query.getQuery());
        }

        List<DbRecord> resultRecords = doSearch(filter, sourceType, limit, page);

        return resultRecords;
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
    private List<DbRecord> doSearch(Bson filter, String collectionName, int limit, int page) throws MongoQueryException {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<DbRecord> dbRecords = new ArrayList<>();

        MongoCursor<Document> cursor;
        cursor = collection
                //.find(Filters.text(query.getQuery()))
                .find(filter)
                .skip(page > 0 ? ((page-1) * limit) : 0)
                .limit(limit)
                .projection(fields(//Projections.metaTextScore("score"),
                        include(
                        ResponseField.TITLE.toString(),
                        ResponseField.YEAR.toString(),
                        ResponseField.ABSTRACT.toString(),
                        ResponseField.AUTHORS.toString(),
                        ResponseField.OWNERS.toString(),
                        ResponseField.DOCUMENT_ID.toString(),
                        ResponseField.DATA_SOURCE.toString())))
                //.sort(Sorts.metaTextScore("score"))
                .iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            dbRecords.add(new DbRecord(document));
        }

        cursor.close();

        return dbRecords;
    }
}

