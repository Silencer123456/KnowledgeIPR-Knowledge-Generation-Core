package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;


import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.PatstatMapper;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a query specification for a single type of query. The child classes
 * create the specifications for concrete target databases.
 *
 * @param <T> - The search instance containing relevant information about a single search,
 *            can be of different types.
 */
public abstract class SearchSpecification<T extends Search> {

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
}
