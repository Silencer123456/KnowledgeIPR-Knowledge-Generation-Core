package kiv.zcu.knowledgeipr.core.sourcedb.datasearch;

/**
 * Enumeration specifying the types of data stored
 * Changing the names of the collections is not recommended, some functions will not work properly
 */
public enum DataSourceType {
    PUBLICATION("publication"),
    PATENT("patent"),
    SPRINGER("springer"),
    ALL("all");

    public final String value;

    DataSourceType(String value) {
        this.value = value;
    }

    public static boolean containsField(String s) {
        for (DataSourceType field : DataSourceType.values()) {
            if (field.value.equals(s)) {
                return true;
            }
        }

        return false;
    }

    public static DataSourceType getByValue(String s) {
        for (DataSourceType field : DataSourceType.values()) {
            if (field.value.equals(s)) {
                return field;
            }
        }

        return null;
    }

    public boolean equalsName(String otherValue) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return value.equals(otherValue);
    }

    public String toString() {
        return this.value;
    }
}
