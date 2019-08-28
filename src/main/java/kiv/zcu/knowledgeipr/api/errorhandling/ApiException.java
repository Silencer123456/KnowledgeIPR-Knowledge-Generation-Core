package kiv.zcu.knowledgeipr.api.errorhandling;

import java.io.Serializable;

public class ApiException extends Exception implements Serializable
{
    private static final long serialVersionUID = 1L;

    public ApiException(String message) {
        super(message);
    }
}