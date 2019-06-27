package kiv.zcu.knowledgeipr.core;

public enum Options {
    /**
     * Specifies if the returned documents should be sorted by their score
     */
    SCORE("score");

    final String value;

    Options(String value) {
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
