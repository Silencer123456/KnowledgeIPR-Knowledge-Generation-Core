package kiv.zcu.knowledgeipr.core.dataaccess;

import org.bson.Document;

//TODO: Probably store only document fields to be used in the response + related docs.

/**
 * Wrapper for MongoDB's document object
 */
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
}
