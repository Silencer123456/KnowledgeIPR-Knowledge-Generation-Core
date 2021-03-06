package kiv.zcu.knowledgeipr.core.knowledgedb.dto;

import java.sql.Timestamp;

public class QueryDto {

    Timestamp lastSubmittedDate;
    private long id;
    private int hash;
    private String rawText;
    private String normalizedText;

    public QueryDto(int hash, String rawText, String normalizedText) {
        this.hash = hash;
        this.rawText = rawText;
        this.normalizedText = normalizedText;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

    public Timestamp getLastSubmittedDate() {
        return lastSubmittedDate;
    }

    public void setLastSubmittedDate(Timestamp lastSubmittedDate) {
        this.lastSubmittedDate = lastSubmittedDate;
    }
}
