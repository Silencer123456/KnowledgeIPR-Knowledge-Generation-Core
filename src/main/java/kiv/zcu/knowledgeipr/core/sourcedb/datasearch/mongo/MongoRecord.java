package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDbRecord;
import org.bson.Document;

/**
 * Wrapper for MongoDB's document object
 */
public class MongoRecord implements IDbRecord {
    private Document document;

    public MongoRecord() {
    }

    public MongoRecord(Document document) {
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

        if (!(obj instanceof MongoRecord)) {
            return false;
        }

        MongoRecord mongoRecord = (MongoRecord) obj;
        return mongoRecord.getDocument().get("_id").toString().equals(this.document.get("_id").toString());
    }
}
