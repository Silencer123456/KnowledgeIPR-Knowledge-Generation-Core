package kiv.zcu.knowledgeipr.core.dbaccess.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;

/**
 * Serves for executing various chartquery gathering statistical information
 *
 * @author Stepan Baratta
 * created on 7/2/2019
 * TODO: Refactor methods into one
 */
public class MongoQueryRunner implements IQueryRunner {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonMongoRunner mongoRunner;

    public MongoQueryRunner(CommonMongoRunner mongoRunner) {
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
    public List<Pair<String, Integer>> activePeople(DataSourceType collectionName, String type, int limit) {
        LOGGER.info("Running 'active " + type + "' method on " + collectionName + " collection.");

        List<Pair<String, Integer>> activeAuthors = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountUnwindAggregation(collectionName, type, type + ".name", limit);

        for (Document doc : output) {
            String author = doc.get(type, Document.class).getString("name");
            activeAuthors.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return activeAuthors;
    }

    @Override
    public List<Pair<String, Integer>> countByArrayField(DataSourceType collectionName, ResponseField field) {
        LOGGER.info("Running query on field " + field.value + " on " + collectionName + " collection.");

        List<Pair<String, Integer>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountUnwindAggregation(collectionName, field.value, field.value, 30);

        for (Document doc : output) {
            String author = String.valueOf(doc.get(field.value));
            fieldToCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return fieldToCounts;
    }

    public List<Pair<String, Integer>> countByField(DataSourceType collectionName, ResponseField field) {
        LOGGER.info("Running query on field " + field.value + " on " + collectionName + " collection.");

        List<Pair<String, Integer>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountAggregation(collectionName, field.value, 30);

        for (Document doc : output) {
            String author = String.valueOf(doc.get(field.value));
            fieldToCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return fieldToCounts;
    }

    @Override
    public List<Pair<Integer, Integer>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category) {
        LOGGER.info("Running getPatentOwnershipEvolutionQuery query on " + owner + " owner and " + category + " category on " + collectionName + " collection.");

        String field = ResponseField.YEAR.value;
        List<Pair<Integer, Integer>> fieldToCounts = new ArrayList<>();


        List<Bson> list = Arrays.asList(
                match(and(Filters.text(category), Filters.eq("owners.name", owner))),
                project(new Document("_id", 0)
                        .append(field, 1)),
                group("$" + field, Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append(field, "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(30));

        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, list);

        for (Document doc : output) {
            //String author = String.valueOf(doc.get(field));
            int author = (Integer) doc.get(field);
            fieldToCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

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
}
