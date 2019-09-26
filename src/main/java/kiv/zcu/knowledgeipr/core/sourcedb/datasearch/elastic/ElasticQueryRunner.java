package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;

import java.util.List;

public class ElasticQueryRunner implements IQueryRunner {
    @Override
    public List<Pair<String, Integer>> countByField(DataSourceType collectionName, ResponseField field) {
        return null;
    }

    @Override
    public List<Pair<String, Integer>> countByArrayField(DataSourceType collectionName, ResponseField field) {
        return null;
    }

    @Override
    public List<Pair<String, Integer>> activePeople(DataSourceType collectionName, String type, int limit) {
        return null;
    }

    @Override
    public List<Pair<Integer, Integer>> getPatentOwnershipEvolutionQuery(DataSourceType collectionName, String owner, String category) {
        /*String field = ResponseField.YEAR.value;
        List<Pair<Integer, Integer>> fieldToCounts = new ArrayList<>();

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
            int author = (Integer) doc.get(field);
            fieldToCounts.add(new Pair<>(author, (Integer) doc.get("count")));
        }

        return fieldToCounts;*/

        return null;
    }
}
