package kiv.zcu.knowledgeipr.core;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import kiv.zcu.knowledgeipr.core.dbconnection.MongoConnection;
import org.bson.Document;

import java.util.Arrays;

import static com.mongodb.client.model.Aggregates.*;

/**
 * @author Stepan Baratta
 * created on 7/2/2019
 */
public class StatsQuery {

    private MongoDatabase database;

    public StatsQuery(MongoConnection connection) {
        database = connection.getConnectionInstance();
    }

    public void createQuery() {
        MongoCollection<Document> collection = database.getCollection("patent");

        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
                project(new Document("_id", 0)
                        .append("authors.name", 1)),
                unwind("$authors"),
                group(new Document("_id", new Document("$toLower", "$authors.name"))
                        .append("count", new Document("$sum", 1))),
                project(new Document("_id", 0).append("authors.name", "$_id").append("count", 1)),
                sort(new Document("count", -1))
        )).allowDiskUse(true);

        for (Document doc : output) {
            System.out.println(doc);
        }
    }
}
