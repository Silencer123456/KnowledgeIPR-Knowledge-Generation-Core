package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.model.Filters;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.core.knowledgedb.dto.ReferenceDto;
import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class enables access to Mongo database and runs search queries on it.
 * Uses the <code>Query</code> class to extract filters from the search and transforms them to
 * the MongoDB's search format.
 */
public class MongoDataSearcher implements IMongoDataSearcher {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonMongoRunner mongoRunner;

    public MongoDataSearcher() {
        this.mongoRunner = CommonMongoRunner.getInstance();
    }

    @Override
    public List<MongoRecord> runSearchAdvanced(Query query, int page, final int limit) throws UserQueryException, QueryExecutionException {
        LOGGER.info("--- ADVANCED SEARCH ---");

        List<MongoRecord> results = runSearchSimple(query, page, limit);
        if (!results.isEmpty()) {
            return results;
        }

        BsonDocument filter = addAllFilters(query, true, true);
        LOGGER.info("Running extended search: " + filter.toJson() + ", page: " + page + ", limit: " + limit + ", timeout: " + query.getOptions().getTimeout());
        // Run second search
        String sourceType = query.getSourceType();

        List<MongoRecord> records;
        try {
            records = mongoRunner.doSearch(sourceType, filter, limit, page, query.getOptions().getTimeout());
        } catch (MongoExecutionTimeoutException | MongoQueryException e) {
            LOGGER.info(e.getMessage());
            throw new QueryExecutionException(e.getMessage());
        }

        return records;
    }

    @Override
    public List<MongoRecord> runSearchSimple(Query query, int page, final int limit) throws UserQueryException, QueryExecutionException {
        String sourceType = query.getSourceType();
        isValidSourceType(sourceType);

        BsonDocument filter = addAllFilters(query, false, false);
        if (filter.isEmpty()) {
            return new ArrayList<>();
        }

        List<MongoRecord> records = new ArrayList<>();

        if (filterContainsIndex(query.getFilters())) {
            LOGGER.info("Running quick search: " + filter.toJson() + ", page: " + page + ", limit: " + limit);
            try {
                records = mongoRunner.doSearch(sourceType, filter, limit, page, 10);
            } catch (MongoExecutionTimeoutException | MongoQueryException e) {
                LOGGER.info("Nothing found during quick search: " + e.getMessage());

                throw new QueryExecutionException(e.getMessage());
            }
        }

        return records;
    }

    @Override
    public List<MongoRecord> searchByReferences(List<ReferenceDto> references) {
        // To list of ObjectIds
        List<ObjectId> urls = references
                .stream()
                .filter(object -> ObjectId.isValid(object.getUrl()))
                .map(object -> new ObjectId(object.getUrl()))
                .collect(Collectors.toList());

        if (urls.isEmpty()) {
            return new ArrayList<>();
        }

        BsonDocument filter = new BsonDocument();
        Bson bson = Filters.in("_id", urls);
        appendBsonDoc(filter, bson, "_id");

        return mongoRunner.doSearch(DataSourceType.PATENT.value, filter, references.size(), 1, 10);
    }

    /**
     * Adds all filters from the search and creates a bson document from them
     *
     * @param query - Query instance from which to extract the filters
     * @return Bson document containing all the filters
     */
    private BsonDocument addAllFilters(Query query, boolean useRegex, boolean useFullText) {
        BsonDocument filter = new BsonDocument();

        addFilters(query.getFilters(), filter, useRegex, useFullText);

        addConditionsFilters(query.getConditions(), filter);

        return filter;
    }

    /**
     * Checks if the provided data source is valid or not
     *
     * @param sourceType - Source of data to check
     * @throws UserQueryException if the source type is not valid
     */
    private void isValidSourceType(String sourceType) throws UserQueryException {
        if (!DataSourceType.containsField(sourceType)) {
            StringBuilder sb = new StringBuilder();
            for (DataSourceType type : DataSourceType.values()) {
                sb.append("'").append(type.value).append("'").append(", ");
            }
            throw new UserQueryException("Unknown data source type: " + sourceType + ". Only " + sb.substring(0, sb.toString().length() - 2) + " allowed.");
        }
    }

    /**
     * Creates Mongo filters according to the specified search
     *
     * @param filters      - list of filters from the search, which will be converted to Mongo filters
     * @param bsonDocument - Bson document, to which add the created filters
     */
    private void addFilters(Map<String, String> filters, BsonDocument bsonDocument, boolean useRegex, boolean useFullText) {
        if (filters == null) return;
        boolean textFilterCreated = false;

        if (filters.containsKey("$text")) {
            Bson tmp = Filters.text(filters.get("$text"));
            appendBsonDoc(bsonDocument, tmp, "$text");
            textFilterCreated = true;
        }

        for (Map.Entry<String, String> filterEntry : filters.entrySet()) {
            boolean isFieldValid = ResponseField.isValid(filterEntry.getKey());
            if (!isFieldValid) {
                if (!filterEntry.getKey().equals("$text")) { // Temp solution
                    LOGGER.warning("Field " + filterEntry.getKey() + " is not valid, skipping.");
                }
                continue;
            }

            Bson tmp;
            // If the text filter was not specified in the search, we create one from the current field
            if (useFullText && !textFilterCreated && isKeyTextIndexed(filterEntry.getKey())) {
                tmp = Filters.text(filterEntry.getValue());
                appendBsonDoc(bsonDocument, tmp, "$text");
                textFilterCreated = true;
            }

            if (useRegex) {
                Pattern regex = Pattern.compile(filterEntry.getValue(), Pattern.CASE_INSENSITIVE);
                tmp = Filters.regex(filterEntry.getKey(), regex);
            } else {
                tmp = Filters.eq(filterEntry.getKey(), filterEntry.getValue());
            }

//            Pattern regex = Pattern.compile(filterEntry.getValue(), Pattern.CASE_INSENSITIVE);
//            Bson r = Filters.regex(filterEntry.getKey(), regex);
//            Bson eq = Filters.eq(filterEntry.getKey(), filterEntry.getValue());
//            tmp = Filters.or(eq, r);

            appendBsonDoc(bsonDocument, tmp, filterEntry.getKey());
            //appendBsonDoc(bsonDocument, tmp, "$or");
        }
    }

    /**
     * Returns true if the key is part of the text index
     * TODO: Dynamically read from Mongo
     *
     * @param key - Key to search
     * @return true if the key is part of text index, false otherwise
     */
    private boolean isKeyTextIndexed(String key) {
        return key.equals("title") ||
                key.equals("authors.name") ||
                key.equals("owners.name") ||
                key.equals("abstract");
    }

    /**
     * Checks if the provided filters map contains field, which is a part of
     * an index in Mongo
     *
     * @param filters Map of filters
     * @return true if the filters contain any of the indexed fields
     */
    // TODO: Dynamically read from Mongo
    private boolean filterContainsIndex(Map<String, String> filters) {
        return filters.containsKey(ResponseField.DOCUMENT_ID.value) ||
                filters.containsKey(ResponseField.TITLE.value) ||
                filters.containsKey(ResponseField.OWNERS_NAME.value);
    }

    /**
     * Appends bson to input Bson document
     *
     * @param bsonDocument - Bson document to which we want to append
     * @param bson         - bson which we want to append to the document
     * @param key          - key under which we want to add to the bson document
     */
    private void appendBsonDoc(BsonDocument bsonDocument, Bson bson, String key) {
        BsonDocument docToAppend = bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());

        if (docToAppend.get(key) == null) return;
        bsonDocument.append(key, docToAppend.get(key));
    }

    /**
     * Creates condition filters from the search.
     * Like gt, lt
     *
     * @param conditions   - List of conditions to be converted to the dataaccess filters
     * @param bsonDocument - Bson document, to which add the option filters
     */
    private void addConditionsFilters(Map<String, Map<String, Integer>> conditions, BsonDocument bsonDocument) {
        if (conditions == null) return;

        for (Map.Entry<String, Map<String, Integer>> optionEntry : conditions.entrySet()) {
            if (optionEntry.getValue() == null) continue;

            Bson tmp;
            if (optionEntry.getValue().containsKey("$gt")) {
                tmp = Filters.gt(optionEntry.getKey(), optionEntry.getValue().get("$gt"));
            } else if (optionEntry.getValue().containsKey("$lt")) {
                tmp = Filters.lt(optionEntry.getKey(), optionEntry.getValue().get("$lt"));
            } else if (optionEntry.getValue().containsKey("$eq")) {
                tmp = Filters.eq(optionEntry.getKey(), optionEntry.getValue().get("$eq"));
            } else {
                tmp = Filters.and();
            }

            appendBsonDoc(bsonDocument, tmp, optionEntry.getKey());
        }
    }
}

