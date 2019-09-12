package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDbRecord;

import java.util.Map;

public class ElasticRecord implements IDbRecord {

    private Map<String, Object> document;

    public ElasticRecord() {
    }

    public ElasticRecord(Map<String, Object> document) {
        this.document = document;
    }

    public void setRecord(Map<String, Object> document) {
        this.document = document;
    }

    public Map<String, Object> getDocument() {
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

        if (!(obj instanceof ElasticRecord)) {
            return false;
        }

        ElasticRecord elasticRecord = (ElasticRecord) obj;
        return elasticRecord.getDocument().get("_id").toString().equals(this.document.get("_id").toString());
    }
}
