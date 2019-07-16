package kiv.zcu.knowledgeipr.core.dbaccess.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.IQueryCreator;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;

/**
 * Serves for executing various queries gathering statistical information
 *
 * @author Stepan Baratta
 * created on 7/2/2019
 * TODO: Refactor methods into one
 */
public class MongoQueryCreator implements IQueryCreator {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonMongoRunner mongoRunner;

    public MongoQueryCreator(CommonMongoRunner mongoRunner) {
        this.mongoRunner = mongoRunner;
    }

    /**
     * Queries Mongo database for most active authors.
     * Query selects 20 most active authors along with the number of their publications/patents
     * and sorts them in descending order.
     * <p>
     * db.patent.aggregate([
     * {$project: { _id: 0, "authors.name": 1 } },
     * {$unwind: "$authors" },
     * {$group: { _id: { $toLower: "$authors.name" }, count: { $sum: 1 } }},
     * {$project: { _id: 0,"authors.name": "$_id", count: 1 } },
     * {$sort: { count: -1 } }
     * ], { allowDiskUse: true })
     *
     * @param collectionName - Collection in which to search
     * @param type           - 'authors' or 'owners'
     * @return - List of 'author name, count' pairs
     */
    @Override
    public List<Pair<String, Integer>> activePeople(String collectionName, String type) {
        LOGGER.info("Running 'active " + type + "' method on " + collectionName + " collection.");

        List<Pair<String, Integer>> activeAuthors = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountUnwindAggregation(collectionName, type, type + ".name", 20);

        for (Document doc : output) {
            String author = doc.get(type, Document.class).getString("name");
            activeAuthors.add(new Pair<>(author, (Integer) doc.get("count")));
        }
//        activeAuthors.add(new Pair<>("test", 20154623));
//        activeAuthors.add(new Pair<>("test", 456));
//        activeAuthors.add(new Pair<>("test", 20146523));
//        activeAuthors.add(new Pair<>("test", 20154623));
//        activeAuthors.add(new Pair<>("test", 20654123));

        return activeAuthors;
    }

    public List<Pair<String, Integer>> countByField(String collectionName, ResponseField field) {
        LOGGER.info("Running query on field " + field.value + " on " + collectionName + " collection.");

        List<Pair<String, Integer>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountAggregation(collectionName, field.value, 30);

        for (Document doc : output) {
            String author = (String) doc.get(field.value);
            fieldToCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

//        fieldToCounts.add(new Pair<>("test", 20123));
//        fieldToCounts.add(new Pair<>("test", 20123));
//        fieldToCounts.add(new Pair<>("test", 20123));
//        fieldToCounts.add(new Pair<>("test", 20123));
//        fieldToCounts.add(new Pair<>("test", 20123));

        return fieldToCounts;
    }

    public int getPeopleCount(String collectionName, String type) {
//        MongoCollection<Document> collection = database.getCollection(collectionName);
//
//        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
//                project(new Document("_id", 0)
//                        .append(type + ".name", 1)),
//                unwind("$" + type),
//                group("$ " + type + ".name", Accumulators.sum("count", 1)),
//                match(gt("count", 1)),
//                count("count"),
//                limit(20)
//        )).allowDiskUse(true);
//
//        for (Document doc : output) {
//            String author = (String) doc.get("fos"); // TODO: change
//        }

        return 0;
    }

    @Override
    public List<Pair<String, Integer>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category) {
        MongoCollection<Document> collection = mongoRunner.getCollection(collectionName.value);

        String field = ResponseField.YEAR.value;
        List<Pair<String, Integer>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = collection.aggregate(Arrays.asList(
                match(and(Filters.text(category), Filters.eq("owners.name", owner))),
                project(new Document("_id", 0)
                        .append(field, 1)),
                group("$" + field, Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append(field, "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(30)
        )).allowDiskUse(true);

        for (Document doc : output) {
            String author = (String) doc.get(field);
            fieldToCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return fieldToCounts;
    }
}
