package kiv.zcu.knowledgeipr.core.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import javafx.util.Pair;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.*;

/**
 * Serves for executing various queries gathering statistical information
 * @author Stepan Baratta
 * created on 7/2/2019
 */
public class StatsRetriever {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private MongoDatabase database;

    public StatsRetriever(MongoConnection connection) {
        database = connection.getConnectionInstance();
    }

    /**
     * Queries Mongo database for most active authors.
     * Query selects 20 most active authors along with the number of their publications/patents
     * and sorts them in descending order.
     *
     * @param collectionName - Collection in which to search
     * @return - List of 'author name, count' pairs
     */
    public List<Pair<String, Integer>> activeAuthors(String collectionName) {
        LOGGER.info("Running 'activeAuthors' method on " + collectionName + " collection.");
        List<Pair<String, Integer>> activeAuthors = new ArrayList<>();

        MongoCollection<Document> collection = database.getCollection(collectionName);

        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
                project(new Document("_id", 0)
                        .append("authors.name", 1)),
                unwind("$authors"),
                group(new Document("$toLower", "$authors.name"),
                        Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append("authors.name", "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(20)
        )).allowDiskUse(true);

        for (Document doc : output) {
            String author = doc.get("authors", Document.class).getString("name");
            activeAuthors.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return activeAuthors;
    }

    /**
     * Queries Mongo database and aggregates fields of study counts
     *
     * @param collectionName - Collection in which to search
     * @return List of 'Field of study, count' pairs
     */
    public List<Pair<String, Integer>> countByFos(String collectionName) {
        LOGGER.info("Running 'countByFos' method on " + collectionName + " collection.");
        List<Pair<String, Integer>> fosCounts = new ArrayList<>();

        MongoCollection<Document> collection = database.getCollection(collectionName);

        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
                project(new Document("_id", 0)
                        .append("authors.name", 1)),
                unwind("$authors"),
                group(new Document("$toLower", "$authors.name"),
                        Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append("authors.name", "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(20)
        )).allowDiskUse(true);

        for (Document doc : output) {
            String author = doc.get("authors", Document.class).getString("name");
            fosCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return fosCounts;
    }
}
