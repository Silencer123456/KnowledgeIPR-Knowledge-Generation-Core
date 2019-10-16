package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.analysis.wordnet.WordNet;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;

/**
 * Main purpose of this class is executing various concrete queries.
 *
 * @author Stepan Baratta
 * created on 7/2/2019
 */
public class MongoQueryRunner implements IQueryRunner {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonMongoRunner mongoRunner;

    public MongoQueryRunner() {
        mongoRunner = CommonMongoRunner.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, Long>> activePeople(DataSourceType collectionName, String type, int limit) {
        LOGGER.info("Running 'active " + type + "' method on " + collectionName + " collection.");

        List<Pair<String, Long>> activeAuthors = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountUnwindAggregation(collectionName, type, type + ".name", limit);

        for (Document doc : output) {
            String author = doc.get(type, Document.class).getString("name");
            activeAuthors.add(new Pair<>(author, (Long) doc.get("count")));
        }

        return activeAuthors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<String, Long>> countByStringArrayField(DataSourceType collectionName, ResponseField field) {
        LOGGER.info("Running search on field " + field.value + " on " + collectionName + " collection.");

        List<Pair<String, Long>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountUnwindAggregation(collectionName, field.value, field.value, 30);

        for (Document doc : output) {
            String author = String.valueOf(doc.get(field.value));
            fieldToCounts.add(new Pair<>(author, (Long) doc.get("count")));
        }

        return fieldToCounts;
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair<String, Long>> countByField(DataSourceType collectionName, ResponseField field) {
        LOGGER.info("Running search on field " + field.value + " on " + collectionName + " collection.");

        List<Pair<String, Long>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountAggregation(collectionName, field.value, 30);

        for (Document doc : output) {
            String author = String.valueOf(doc.get(field.value));
            fieldToCounts.add(new Pair<>(author, (Long) doc.get("count")));
        }

        return fieldToCounts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Pair<Long, Long>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category) {
        String field = ResponseField.YEAR.value;
        List<Pair<Long, Long>> fieldToCounts = new ArrayList<>();

        String categoryStr = WordNet.getInstance().getSynonymsForWordString(category);

        List<Bson> list = Arrays.asList(
                match(and(Filters.text(categoryStr), Filters.eq("owners.name", owner))),
                project(new Document("_id", 0)
                        .append(field, 1)),
                group("$" + field, Accumulators.sum("count", 1)),
                project(new Document("_id", 0).append(field, "$_id").append("count", 1)),
                sort(new Document("count", -1)),
                limit(30));

        AggregateIterable<Document> output = mongoRunner.runAggregation(collectionName, list);

        for (Document doc : output) {
            //String author = String.valueOf(doc.get(field));
            long author = (Long) doc.get(field);
            fieldToCounts.add(new Pair<>(author, (Long) doc.get("count")));
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
