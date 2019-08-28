package kiv.zcu.knowledgeipr.api.errorhandling;

import java.io.Serializable;

/**
 * @author Stepan Baratta
 * created on 6/25/2019
 */
public class UserQueryException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserQueryException(String msg) {
        super(msg);
    }

    public UserQueryException(String msg, Exception e) {
        super(msg, e);

    }
}
