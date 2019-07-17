package kiv.zcu.knowledgeipr.core.dbaccess.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import com.mongodb.client.model.Filters;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.DbRecord;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Accesses the source database, runs queries on it and gets
 * a results set back.
 */
public class DataRetriever {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private CommonMongoRunner mongoRunner;

    public DataRetriever(CommonMongoRunner mongoRunner) {
        this.mongoRunner = mongoRunner;
    }

    /**
     * Runs the query on the source database and returns a result set.
     * First a quick query is run, performing an exact match search, which should be very fast when using index.
     * This query is limited to just few seconds of execution.
     * If no results are returned by that time, the second main query is run with user
     * specified timeout.
     *
     * @param query - knowledgeipr.Query to be run
     * @param page  - Page to return
     * @param limit - Limit of the returned results
     * @return - Result list of <code>knowledgeipr.DbRecord</code> instances.
     */
    public List<DbRecord> runQuery(Query query, int page, final int limit) throws MongoQueryException, UserQueryException, MongoExecutionTimeoutException {
        String sourceType = query.getSourceType();
        isValidSourceType(sourceType);

        BsonDocument filter = addAllFilters(query, false, false);
        if (filter.isEmpty()) {
            return Collections.emptyList();
        }

        if (filterContainsIndex(query.getFilters())) {
            LOGGER.info("Running 1. query: " + filter.toJson() + ", page: " + page + ", limit: " + limit);
            try {
                List<DbRecord> results = mongoRunner.doSearch(sourceType, filter, limit, page, 10);
                // If something was found, we do not need to run the second query
                if (!results.isEmpty()) {
                    return results;
                }
            } catch (MongoExecutionTimeoutException e) {
                LOGGER.info("Nothing found during quick query");
            }
        }

        filter = addAllFilters(query, true, true);

        LOGGER.info("Running 2. query: " + filter.toJson() + ", page: " + page + ", limit: " + limit + ", timeout: " + query.getOptions().getTimeout());
        // Run second query
        return mongoRunner.doSearch(sourceType, filter, limit, page, query.getOptions().getTimeout());
    }

    /**
     * Adds all filters from the query and creates a bson document from them
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
     * Creates Mongo filters according to the specified query
     *
     * @param filters      - list of filters from the query, which will be converted to Mongo filters
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
                LOGGER.warning("Field " + filterEntry.getKey() + " is not valid, skipping.");
                continue;
            }

            Bson tmp;
            // If the text filter was not specified in the query, we create one from the current field
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
        //TODO: get info directly from Mongo
        return key.equals("title") ||
                key.equals("authors.name") ||
                key.equals("owners.name") ||
                key.equals("abstract");
    }
    // TODO: Dynamically read from Mongo

    /**
     * Checks if the provided filters map contains field, which is a part of
     * an index in Mongo
     *
     * @param filters Map of filters
     * @return true if the filters contain any of the indexed fields
     */
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
     * Creates condition filters from the query.
     * Like gt, lt
     *
     * @param conditions   - List of conditions to be converted to the dbaccess filters
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

