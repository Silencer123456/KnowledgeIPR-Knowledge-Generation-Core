package kiv.zcu.knowledgeipr.core.dbconnection;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Provides a single point of access to the MongoDB database
 *
 * @author Stepan Baratta
 * created on 7/2/2019
 */
public class MongoConnection {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    /**
     * Default name of the Mongo database, in case the configuration file is not found
     */
    private static final String DEFAULT_DB_NAME = "knowingipr";
    /**
     * Path to the mongo configuration file
     */
    private static final String MONGO_CONFIG_PATH = "mongo-config.cfg";
    /**
     * Singleton instance
     */
    private static MongoConnection instance;
    private String dbName = DEFAULT_DB_NAME;

    /**
     * Mongo database access instance
     */
    private MongoDatabase database;

    private MongoConnection() {
        setup();
    }

    public static MongoConnection getInstance() {
        if (instance == null) {
            instance = new MongoConnection();
        }

        return instance;
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

    public MongoDatabase getConnectionInstance() {
        return this.database;
    }
}
