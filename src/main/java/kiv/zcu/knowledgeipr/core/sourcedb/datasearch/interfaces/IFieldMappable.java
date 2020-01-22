package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;

public interface IFieldMappable {

    /**
     * Returns the correct field name mapped to the standardized field passed in the parameter
     *
     * @param field - The standardized field for which to find the concrete mapped field
     * @return mapped field
     */
    String getMapping(ResponseField field);
}
