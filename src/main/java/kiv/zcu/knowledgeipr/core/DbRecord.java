package kiv.zcu.knowledgeipr.core;

import org.bson.Document;

/**
 * TODO: Probably store only document fields to be used in the response + related docs.
 */
public class DbRecord {
    private Document document;

    // TODO: handle related documents here
    //private List<Document> relatedDocuments;

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
