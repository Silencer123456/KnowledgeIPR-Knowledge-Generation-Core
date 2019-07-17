package kiv.zcu.knowledgeipr.rest.errorhandling;

import java.io.Serializable;

public class ObjectSerializationException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    public ObjectSerializationException(String message) {
        super(message);
    }

    public ObjectSerializationException(String message, Exception e) {
        super(message, e);
    }
}
