package kiv.zcu.knowledgeipr.core.dbaccess;

/**
 * Enumeration specifying the types of data stored
 */
public enum DataSourceType {

    PUBLICATION("publication"),
    PATENT("patent");

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

    public boolean equalsName(String otherValue) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return value.equals(otherValue);
    }

    public String toString() {
        return this.value;
    }
}
