package kiv.zcu.knowledgeipr.core;

// TODO: Separate fields for patents and publications into its own enums
public enum ResponseField {
    DOCUMENT_ID("number"),
    TITLE("title"),
    ABSTRACT("abstract"),
    YEAR("year"),
    DATE("date"),
    AUTHORS("authors"),
    AUTHORS_NAME("authors.name"),
    OWNERS("owners"),
    DATA_SOURCE("dataSource"),
    PUBLISHER("publisher"),
    FOS("fos"),
    ISSUE("issue"),
    URL("url"),
    KEYWORDS("keywords"),
    VENUE("venue"),
    LANG("lang"),
    DOI("doi"),
    STATUS("status"),
    COUNTRY("country");

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
