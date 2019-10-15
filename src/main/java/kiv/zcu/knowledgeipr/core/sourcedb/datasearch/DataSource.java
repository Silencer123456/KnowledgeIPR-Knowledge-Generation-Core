package kiv.zcu.knowledgeipr.core.sourcedb.datasearch;

/**
 * Enumeration specifying the types of data stored
 * Changing the names of the collections is not recommended, some functions will not work properly
 */
public enum DataSource {
    SPRINGER("springer"),
    USPTO("patent"), // todo: Rename value to USPTO
    MAG("publication"); // todo: Rename value to MAG

    public final String value;

    DataSource(String value) {
        this.value = value;
    }

    public static boolean containsField(String s) {
        for (DataSource field : DataSource.values()) {
            if (field.value.equals(s)) {
                return true;
            }
        }

        return false;
    }

    public static DataSource getByValue(String s) {
        for (DataSource field : DataSource.values()) {
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
