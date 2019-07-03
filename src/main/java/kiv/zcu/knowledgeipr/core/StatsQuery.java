package kiv.zcu.knowledgeipr.core;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbconnection.MongoConnection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.*;

/**
 * @author Stepan Baratta
 * created on 7/2/2019
 */
public class StatsQuery {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private MongoDatabase database;

    public StatsQuery(MongoConnection connection) {
        database = connection.getConnectionInstance();
    }

    public List<Pair<String, Integer>> activeAuthors() {
        LOGGER.info("Running 'getActiveAuthors' method on database.");
        List<Pair<String, Integer>> activeAuthors = new ArrayList<>();

        MongoCollection<Document> collection = database.getCollection("patent");

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
}
