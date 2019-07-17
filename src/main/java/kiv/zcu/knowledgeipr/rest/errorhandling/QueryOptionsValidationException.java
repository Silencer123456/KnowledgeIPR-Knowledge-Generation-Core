package kiv.zcu.knowledgeipr.rest.errorhandling;

public class QueryOptionsValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public QueryOptionsValidationException(String msg) {
        super(msg);
    }

    public QueryOptionsValidationException(String msg, Exception e) {
        super(msg, e);

    }
}
