package kiv.zcu.knowledgeipr.core;

public enum ResponseField {
    DOCUMENT_ID("documentId"),
    TITLE("title"),
    ABSTRACT("abstract"),
    YEAR("year"),
    AUTHORS("authors"),
    AUTHORS_NAME("authors.name"),
    OWNERS("owners"),
    DATA_SOURCE("dataSource");

    final String value;

    ResponseField(String value) {
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
