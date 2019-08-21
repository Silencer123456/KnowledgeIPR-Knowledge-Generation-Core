package kiv.zcu.knowledgeipr.core.search;

import kiv.zcu.knowledgeipr.rest.errorhandling.QueryOptionsValidationException;

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
        if (options.containsKey("timeout")) {
            int timeout = (int) options.get("timeout");
            if (timeout < 0) {
                timeout = DEFAULT_TIMEOUT;
            }
            timeout = Math.min(timeout, MAX_TIMEOUT);
            return timeout;
        } else {
            return DEFAULT_TIMEOUT;
        }
    }

    /**
     * Validates the provided options
     * TODO: get option names from enum
     *
     * @throws QueryOptionsValidationException if the validation fails
     */
    public void validate() throws QueryOptionsValidationException {
        if (options.containsKey("timeout")) {
            if (!(options.get("timeout") instanceof Integer)) {
                throw new QueryOptionsValidationException("Value of field timeout must be an integer");
            }
        }
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    enum Options {
        /**
         * Specifies if the returned documents should be sorted by their score
         */
        SCORE("score");

        final String value;

        Options(String value) {
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
