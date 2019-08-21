package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.model.Filters;
import kiv.zcu.knowledgeipr.core.dataaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dataaccess.DbRecord;
import kiv.zcu.knowledgeipr.core.dataaccess.ResponseField;
import kiv.zcu.knowledgeipr.core.search.Query;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Class enables access to Mongo database and runs search queries on it.
 * Uses the <code>Query</code> class to extract filters from the search and transforms them to
 * the MongoDB's search format.
 */
public class MongoDataSearcher implements IDataSearcher {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonMongoRunner mongoRunner;

    public MongoDataSearcher() {
        this.mongoRunner = CommonMongoRunner.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DbRecord> runSearchAdvanced(Query query, int page, final int limit) throws MongoQueryException, UserQueryException, MongoExecutionTimeoutException {
        LOGGER.info("Running advanced search");

        String sourceType = query.getSourceType();
        isValidSourceType(sourceType);

        BsonDocument filter = addAllFilters(query, false, false);
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        if (filterContainsIndex(query.getFilters())) {
            LOGGER.info("Running quick search: " + filter.toJson() + ", page: " + page + ", limit: " + limit);
            try {
                List<DbRecord> results = mongoRunner.doSearch(sourceType, filter, limit, page, 10);
                // If something was found, we do not need to runAggregation the second search
                if (!results.isEmpty()) {
                    return results;
                }
            } catch (MongoExecutionTimeoutException e) {
                LOGGER.info("Nothing found during quick search");
            }
        }

        filter = addAllFilters(query, true, true);

        LOGGER.info("Running extended search: " + filter.toJson() + ", page: " + page + ", limit: " + limit + ", timeout: " + query.getOptions().getTimeout());
        // Run second search
        return mongoRunner.doSearch(sourceType, filter, limit, page, query.getOptions().getTimeout());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DbRecord> runSearchSimple(Query query, int page, final int limit) throws MongoQueryException, UserQueryException, MongoExecutionTimeoutException {
        LOGGER.info("Running simple search");

        String sourceType = query.getSourceType();
        isValidSourceType(sourceType);

        BsonDocument filter = addAllFilters(query, false, false);
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        List<DbRecord> records = new ArrayList<>();

        if (filterContainsIndex(query.getFilters())) {
            LOGGER.info("Running 1. search: " + filter.toJson() + ", page: " + page + ", limit: " + limit);
            try {
                records = mongoRunner.doSearch(sourceType, filter, limit, page, 10);
            } catch (MongoExecutionTimeoutException e) {
                LOGGER.info("Nothing found during quick search");
            }
        }

        return records;
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
            throw new UserQueryException("Unknown data source type: " + sourceType + ". Only 'patent' and 'publication' allowed");
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
     * @param key          - key that we want to add to the bson document
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

