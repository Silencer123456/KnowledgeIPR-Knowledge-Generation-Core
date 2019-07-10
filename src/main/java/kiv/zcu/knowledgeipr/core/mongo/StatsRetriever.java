package kiv.zcu.knowledgeipr.core.mongo;

import com.mongodb.client.AggregateIterable;
import javafx.util.Pair;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Serves for executing various queries gathering statistical information
 * @author Stepan Baratta
 * created on 7/2/2019
 */
public class StatsRetriever {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private MongoRunner mongoRunner;

    public StatsRetriever(MongoRunner mongoRunner) {
        this.mongoRunner = mongoRunner;
    }

    /**
     * Queries Mongo database for most active authors.
     * Query selects 20 most active authors along with the number of their publications/patents
     * and sorts them in descending order.
     *
     * db.patent.aggregate([
     *   {$project: { _id: 0, "authors.name": 1 } },
     *   {$unwind: "$authors" },
     *   {$group: { _id: { $toLower: "$authors.name" }, count: { $sum: 1 } }},
     *   {$project: { _id: 0,"authors.name": "$_id", count: 1 } },
     *   {$sort: { count: -1 } }
     * ], { allowDiskUse: true })
     *
     * @param collectionName - Collection in which to search
     * @param type - 'authors' or 'owners'
     * @return - List of 'author name, count' pairs
     */
    public List<Pair<String, Integer>> activePeople(String collectionName, String type) {
        LOGGER.info("Running 'active " + type + "' method on " + collectionName + " collection.");

        List<Pair<String, Integer>> activeAuthors = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runUnwindAggregation(collectionName, type, type + ".name", 20);

        for (Document doc : output) {
            String author = doc.get(type, Document.class).getString("name");
            activeAuthors.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return activeAuthors;
    }

    /**
     * Queries Mongo database and aggregates fields of study counts
     *
     * db.publication.aggregate([
     *   {$project: { _id: 0, fos: 1 } },
     *   {$unwind: "$fos" },
     *   {$group: { _id: "$fos", tags: { $sum: 1 } }},
     *   {$project: { _id: 0, fos: "$_id", tags: 1 } },
     *   {$sort: { tags: -1 } }
     * ])
     *
     * @param collectionName - Collection in which to search
     * @return List of 'Field of study, count' pairs
     */
    public List<Pair<String, Integer>> countByFos(String collectionName) {
        LOGGER.info("Running 'countByFos' method on " + collectionName + " collection.");
        List<Pair<String, Integer>> fosCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runUnwindAggregation(collectionName, "fos", "fos", 20);

        for (Document doc : output) {
            String author = (String) doc.get("fos");
            fosCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return fosCounts;
    }

    public List<Pair<Integer, Integer>> countByYear(String collectionName) {
        LOGGER.info("Running 'countByYear' method on " + collectionName + " collection.");
        List<Pair<Integer, Integer>> yearCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, "year", 20);

        for (Document doc : output) {
            int author = (Integer) doc.get("year");
            yearCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return yearCounts;
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

    public List<Pair<String, Integer>> createTagCloud(String collectionName, String textSearch) {
        return Collections.emptyList();
    }
}
