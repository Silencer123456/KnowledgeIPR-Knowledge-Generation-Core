package kiv.zcu.knowledgeipr.core;

public enum ResponseField {
    DOCUMENT_ID("number"),
    TITLE("title"),
    ABSTRACT("abstract"),
    YEAR("year"),
    AUTHORS("authors"),
    AUTHORS_NAME("authors.name"),
    OWNERS("owners"),
    DATA_SOURCE("dataSource"),
    PUBLISHER("publisher"),
    FOS("fos");

    public final String value;

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

    public static boolean isValid(String s) {
        for (ResponseField field : ResponseField.values()) {
            if (field.value.equals(s)) {
                return true;
            }
        }

        return false;
    }
}
