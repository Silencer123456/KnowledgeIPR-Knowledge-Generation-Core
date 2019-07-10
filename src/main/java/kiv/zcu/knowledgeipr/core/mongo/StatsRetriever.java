package kiv.zcu.knowledgeipr.core.mongo;

import com.mongodb.client.AggregateIterable;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.ResponseField;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Serves for executing various queries gathering statistical information
 * @author Stepan Baratta
 * created on 7/2/2019
 * TODO: Refactor methods into one
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
//        activeAuthors.add(new Pair<>("test", 20154623));
//        activeAuthors.add(new Pair<>("test", 456));
//        activeAuthors.add(new Pair<>("test", 20146523));
//        activeAuthors.add(new Pair<>("test", 20154623));
//        activeAuthors.add(new Pair<>("test", 20654123));
//        activeAuthors.add(new Pair<>("test", 54));
//        activeAuthors.add(new Pair<>("test", 20123));

        return activeAuthors;
    }

//    public List<Pair<String, Integer>> countByYear(String collectionName) {
//        LOGGER.info("Running 'countByYear' method on " + collectionName + " collection.");
//        List<Pair<String, Integer>> yearCounts = new ArrayList<>();
//
//        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, ResponseField.YEAR.value, 20);
//
//        for (Document doc : output) {
//            String author = (String) doc.get(ResponseField.YEAR.value);
//            yearCounts.add(new Pair<>(author, (Integer) doc.get("count")));
//        }
//
//        return yearCounts;
//    }
//    public List<Pair<String, Integer>> prolificPublishers(String collectionName) {
//        LOGGER.info("Running 'prolificPublishers' method on " + collectionName + " collection.");
//        String fieldName = ResponseField.PUBLISHER.value;
//
//        List<Pair<String, Integer>> prolificPublishers = new ArrayList<>();
//
//        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, fieldName, 30);
//
//        for (Document doc : output) {
//            String author = (String) doc.get(fieldName);
//            prolificPublishers.add(new Pair<>(author, (Integer) doc.get("count")));
//        }
//
//        return prolificPublishers;
//    }
//    /**
//     * Queries Mongo database and aggregates fields of study counts
//     *
//     * db.publication.aggregate([
//     *   {$project: { _id: 0, fos: 1 } },
//     *   {$unwind: "$fos" },
//     *   {$group: { _id: "$fos", tags: { $sum: 1 } }},
//     *   {$project: { _id: 0, fos: "$_id", tags: 1 } },
//     *   {$sort: { tags: -1 } }
//     * ])
//     *
//     * @param collectionName - Collection in which to search
//     * @return List of 'Field of study, count' pairs
//     */
//    public List<Pair<String, Integer>> countByFos(String collectionName) {
//        LOGGER.info("Running 'countByFos' method on " + collectionName + " collection.");
//        List<Pair<String, Integer>> fosCounts = new ArrayList<>();
//
//        AggregateIterable<Document> output = mongoRunner.runUnwindAggregation(collectionName, ResponseField.FOS.value, ResponseField.FOS.value, 30);
//
//        for (Document doc : output) {
//            String author = (String) doc.get(ResponseField.FOS.value);
//            fosCounts.add(new Pair<>(author, (Integer) doc.get("count")));
//        }
//
//        return fosCounts;
//    }

//    public List<Pair<String, Integer>> countByKeyword(String collectionName) {
//        LOGGER.info("Running 'countByKeyword' method on " + collectionName + " collection.");
//        List<Pair<String, Integer>> keywordsCount = new ArrayList<>();
//
//        AggregateIterable<Document> output = mongoRunner.runUnwindAggregation(collectionName, ResponseField.KEYWORDS.value, ResponseField.KEYWORDS.value, 30);
//
//        for (Document doc : output) {
//            String author = (String) doc.get(ResponseField.KEYWORDS.value);
//            keywordsCount.add(new Pair<>(author, (Integer) doc.get("count")));
//        }
//
//        return keywordsCount;
//    }

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

//    public List<Pair<String, Integer>> countByVenue(String collectionName) {
//        LOGGER.info("Running 'prolificVenues' method on " + collectionName + " collection.");
//        String fieldName = ResponseField.VENUE.value;
//
//        List<Pair<String, Integer>> prolificPublishers = new ArrayList<>();
//
//        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, fieldName, 30);
//
//        for (Document doc : output) {
//            String author = (String) doc.get(fieldName);
//            prolificPublishers.add(new Pair<>(author, (Integer) doc.get("count")));
//        }
//
//        return prolificPublishers;
//    }
//
//    public List<Pair<String, Integer>> countByLang(String collectionName) {
//        LOGGER.info("Running 'prolificVenues' method on " + collectionName + " collection.");
//        String fieldName = ResponseField.VENUE.value;
//
//        List<Pair<String, Integer>> prolificPublishers = new ArrayList<>();
//
//        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, fieldName, 30);
//
//        for (Document doc : output) {
//            String author = (String) doc.get(fieldName);
//            prolificPublishers.add(new Pair<>(author, (Integer) doc.get("count")));
//        }
//
//        return prolificPublishers;
//    }

    public List<Pair<String, Integer>> countByField(String collectionName, ResponseField field) {
        LOGGER.info("Running query on field " + field.value + " on " + collectionName + " collection.");

        List<Pair<String, Integer>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, field.value, 30);

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


}
