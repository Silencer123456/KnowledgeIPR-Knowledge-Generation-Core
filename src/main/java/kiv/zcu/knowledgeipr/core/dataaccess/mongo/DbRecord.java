package kiv.zcu.knowledgeipr.core.dataaccess.mongo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.Document;

// TODO: TEMPORARY CLASS!!! Will be made abstract, implementations will be created for concrete dbs
/**
 * Wrapper for MongoDB's document object
 */
@JsonIgnoreProperties(value = {"_id"})
public class DbRecord {
    private Document document;

    public DbRecord() {
    }

    public DbRecord(Document document) {
        this.document = document;
    }

    public void setRecord(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    /**
     * The equality of two objects is based on the _id field
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DbRecord)) {
            return false;
        }

        DbRecord dbRecord = (DbRecord) obj;
        return dbRecord.getDocument().get("_id").toString().equals(this.document.get("_id").toString());
    }
}
