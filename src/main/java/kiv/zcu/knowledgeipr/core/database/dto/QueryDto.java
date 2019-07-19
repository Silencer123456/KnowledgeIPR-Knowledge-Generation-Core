package kiv.zcu.knowledgeipr.core.database.dto;

import java.time.LocalDate;

public class QueryDto {

    LocalDate lastSubmittedDate;
    private int id;
    private String hash;
    private String rawText;
    private String normalizedText;

    public QueryDto(String hash, String rawText, String normalizedText) {
        this.hash = hash;
        this.rawText = rawText;
        this.normalizedText = normalizedText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
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

    public LocalDate getLastSubmittedDate() {
        return lastSubmittedDate;
    }

    public void setLastSubmittedDate(LocalDate lastSubmittedDate) {
        this.lastSubmittedDate = lastSubmittedDate;
    }
}
