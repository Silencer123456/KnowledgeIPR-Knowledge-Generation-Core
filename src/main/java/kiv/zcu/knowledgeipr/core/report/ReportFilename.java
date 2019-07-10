package kiv.zcu.knowledgeipr.core.report;

/**
 * @author Stepan Baratta
 * created on 7/10/2019
 */
public enum ReportFilename {
    COUNT_BY_YEAR("countByYear.json"),
    COUNT_BY_FOS("countByFos.json"),
    COUNT_BY_KEYWORD("countByKeyword.json"),
    ACTIVE_AUTHORS("activeauthors.json"),
    ACTIVE_OWNERS("activeowners.json"),
    COUNT_BY_PUBLISHER("prolificPublishers.json"),
    COUNT_BY_VENUES("prolificVenues.json");

    public final String value;

    ReportFilename(String value) {
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
