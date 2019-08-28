package kiv.zcu.knowledgeipr.core.model.search.category.data;

import java.io.Serializable;

public class CategoryReadException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    public CategoryReadException(String message) {
        super(message);
    }
}
