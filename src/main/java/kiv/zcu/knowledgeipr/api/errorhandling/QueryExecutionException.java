package kiv.zcu.knowledgeipr.api.errorhandling;

import java.io.Serializable;

/**
 * A database independent specification of an exception which occurred during execution of a query.
 */
public class QueryExecutionException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public QueryExecutionException(String msg) {
        super(msg);
    }

    public QueryExecutionException(String msg, Exception e) {
        super(msg, e);

    }
}
