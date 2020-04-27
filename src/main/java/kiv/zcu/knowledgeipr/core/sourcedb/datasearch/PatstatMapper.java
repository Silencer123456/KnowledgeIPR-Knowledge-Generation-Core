package kiv.zcu.knowledgeipr.core.sourcedb.datasearch;

public class PatstatMapper {

    /**
     * Returns mapped fields specific to PATSTAT database
     */
    /**
     * Returns the correct field name mapped to the standardized field passed in the parameter.
     * In this case mapped fields specific to PATSTAT database.
     *
     * @param field - The standardized field for which to find the concrete mapped field
     * @return mapped field
     */
    public static String getMapping(ResponseField field) {
        String result = "";
        switch (field) {
            case TITLE:
                result = "applnTitle.title";
                break;
            case ABSTRACT:
                result = "applnAbstr.abstract";
                break;
            case AUTHORS_NAME:
                result = "authors.person_name";
                break;
            case DATE:
                result = "earliest_publn_date";
                break;
        }
        return result;
    }
}
