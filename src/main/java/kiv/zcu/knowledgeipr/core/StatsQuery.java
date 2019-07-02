package kiv.zcu.knowledgeipr.core;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import kiv.zcu.knowledgeipr.core.dbconnection.MongoConnection;
import org.bson.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Aggregates.*;

/**
 *
 * TODO: Refactor
 * @author Stepan Baratta
 * created on 7/2/2019
 */
public class StatsQuery {

    private MongoDatabase database;

    public StatsQuery(MongoConnection connection) {
        database = connection.getConnectionInstance();
    }

    public void createQuery() {
        Map<String, Long> activeAuthors = new HashMap<>();

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
            activeAuthors.put((String) doc.get("authors.name"), (Long) doc.get("count"));
            System.out.println(doc);
        }
    }
}
