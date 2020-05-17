package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;


import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.PatstatMapper;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Provides a query specification for a single type of query. The child classes
 * create the specifications for concrete target databases.
 *
 * @param <T> - The search instance containing relevant information about a single search,
 *            can be of different types.
 */
public abstract class SearchSpecification<T extends Search> {

    protected final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Search instance containing relevant information about a search
     */
    protected T search;

    public SearchSpecification(T search) {
        this.search = search;
    }

    /**
     * Returns the constructed query for the target database.
     * todo: change return type to be generic
     * @return - The constructed query
     */
    public abstract QueryBuilder get();

    public T getSearch() {
        return search;
    }

    /**
     * Returns a set of fields which will be used as a default set
     * on which to perform the search
     *
     * @return A map of default fields
     */
    protected Map<String, Float> getDefaultFieldsMap() {
        Map<String, Float> fieldsMap = new HashMap<>();
        fieldsMap.put(ResponseField.TITLE.value, 1F);
        fieldsMap.put(ResponseField.ABSTRACT.value, 1F);
        fieldsMap.put(ResponseField.AUTHORS_NAME.value, 1F);
        fieldsMap.put(PatstatMapper.getPatstatMapping(ResponseField.TITLE), 1F);
        fieldsMap.put(PatstatMapper.getPatstatMapping(ResponseField.ABSTRACT), 1F);
        fieldsMap.put(PatstatMapper.getPatstatMapping(ResponseField.AUTHORS_NAME), 1F);

        return fieldsMap;
    }

    /**
     * Creates a list with all the mapped fields for every data set.
     * If no fields are specified, a default list is used.
     *
     * @param queryFields - The initial list of fields for which to find the mapped fields
     * @return Map of all the mapped fields and their weight (equals 1 every time)
     */
    protected Map<String, Float> getAllMappedFields(final List<String> queryFields) {
        Map<String, Float> mappedFields = new HashMap<>();

        for (String field : queryFields) {
            ResponseField foundField = ResponseField.getNameFromValue(field);
            if (foundField != null) {
                mappedFields.put(field, 1F);
                mappedFields.put(PatstatMapper.getPatstatMapping(foundField), 1F);
            } else {
                LOGGER.warning("The field " + field + " is not a valid search field and will be skipped.");
            }
        }

        if (mappedFields.isEmpty()) {
            mappedFields = getDefaultFieldsMap();
            LOGGER.info("No fields were accepted, using the default set.");
        }

        return mappedFields;
    }
}
