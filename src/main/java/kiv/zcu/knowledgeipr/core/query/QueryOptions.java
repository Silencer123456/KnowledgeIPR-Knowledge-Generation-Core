package kiv.zcu.knowledgeipr.core.query;

import kiv.zcu.knowledgeipr.rest.errorhandling.QueryOptionsValidationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages query options
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
}
