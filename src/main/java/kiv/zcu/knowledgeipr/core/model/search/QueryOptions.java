package kiv.zcu.knowledgeipr.core.model.search;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryOptionsValidationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages search options
 */
public class QueryOptions {

    /**
     * Default timeout value
     */
    private static final int DEFAULT_TIMEOUT = 100;
    private static final int MAX_TIMEOUT = 1000;

    private Map<String, Object> options;

    /**
     * Default constructor, assigns options map parameter if not null
     *
     * @param options
     */
    public QueryOptions(Map<String, Object> options) {
        this.options = options == null ? new HashMap<>() : options;
    }

    /**
     * Gets the value of the timeout field.
     * If the value is not specified, the default value of 999 is set
     *
     * @return timeout
     */
    public int getTimeout() {
        if (options.containsKey(QueryOption.TIMEOUT.value)) {
            return (int) options.get(QueryOption.TIMEOUT.value);
        } else {
            return DEFAULT_TIMEOUT;
        }
    }

    /**
     * Gets the specified query option value as an Object. If the
     * option is not present, null is returned
     *
     * @param option - The option to find the value for
     * @return
     */
    public Object getOption(QueryOption option) {
        Object o = null;
        if (options.containsKey(option.value)) {
            o = options.get(option.value);
        }

        return o;
    }

    /**
     * Validates the provided options
     * TODO: get option names from enum
     *
     * @throws QueryOptionsValidationException if the validation fails
     */
    public void validate() throws QueryOptionsValidationException {
        String timeoutVal = QueryOption.TIMEOUT.value;
        if (options.containsKey(timeoutVal)) {
            if (!(options.get(timeoutVal) instanceof Integer)) {
                throw new QueryOptionsValidationException("Value of field timeout must be an integer");
            }

            int timeout = (int) options.get(timeoutVal);
            if (timeout < 0) {
                timeout = DEFAULT_TIMEOUT;
            }
            timeout = Math.min(timeout, MAX_TIMEOUT);
            options.replace(timeoutVal, options.get(timeoutVal), timeout);
        }

        if (options.containsKey(QueryOption.USE_CACHE.value)) {
            if (!(options.get(QueryOption.USE_CACHE.value) instanceof Boolean)) {
                throw new QueryOptionsValidationException("Value of field " + QueryOption.USE_CACHE.value + " must be boolean");
            }
        }
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public enum QueryOption {
        /**
         * Specifies if the returned documents should be sorted by their score
         */
        SCORE("score"),
        TIMEOUT("timeout"),
        USE_CACHE("useCache");

        final String value;

        QueryOption(String value) {
            this.value = value;
        }

        public boolean equalsName(String otherValue) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return value.equals(otherValue);
        }

        public String toString() {
            return this.value;
        }
    }
}
