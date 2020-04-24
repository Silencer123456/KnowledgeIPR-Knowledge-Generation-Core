package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.analysis.wordnet.WordNet;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSource;
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
    public List<Pair<String, Integer>> activePeople(DataSource collectionName, String type, int limit) {
        LOGGER.info("Running 'active " + type + "' method on " + collectionName + " collection.");

        List<Pair<String, Integer>> activeAuthors = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountUnwindAggregation(collectionName, type, type + ".name", limit);

        for (Document doc : output) {
            String author = doc.get(type, Document.class).getString("name");
            activeAuthors.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return activeAuthors;
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public List<Pair<Object, Long>> countByStringArrayField(List<DataSource> collections, ResponseField field) {
        LOGGER.info("Running search on field " + field.value + " on indexes: " + collections + ".");

        List<Pair<Object, Long>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountUnwindAggregation(collections.get(0), field.value, field.value, 30);

        for (Document doc : output) {
            String author = String.valueOf(doc.get(field.value));
            fieldToCounts.add(new Pair<>(author, (Long) doc.get("count")));
        }

        return fieldToCounts;
    }

    /**
     * {@inheritDoc}
     */
    public List<Pair<Object, Long>> countByField(List<DataSource> collections, ResponseField field) {
        LOGGER.info("Running search on field " + field.value + " on " + collections + " collections.");

        List<Pair<Object, Long>> fieldToCounts = new ArrayList<>();

        AggregateIterable<Document> output = mongoRunner.runCountAggregation(collections.get(0), field.value, 30);

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
    public List<Pair<Long, Long>> patentOwnershipEvolution(List<DataSource> collections, String owner, String category) {
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

        AggregateIterable<Document> output = mongoRunner.runAggregation(collections.get(0), list);

        for (Document doc : output) {
            //String author = String.valueOf(doc.get(field));
            long author = (Long) doc.get(field);
            fieldToCounts.add(new Pair<>(author, (Long) doc.get("count")));
        }

        return fieldToCounts;
    }

    @Override
    public List<Pair<Long, Long>> dateHistogram(List<DataSource> collections) throws QueryExecutionException {
        return null;
    }
}
