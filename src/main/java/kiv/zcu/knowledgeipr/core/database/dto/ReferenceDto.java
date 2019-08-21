package kiv.zcu.knowledgeipr.core.database.dto;

import java.sql.Timestamp;

public class ReferenceDto {

    private long id;
    private String url;
    private Timestamp lastCheckDate;

    /**
     * Needs empty constructor for the serialization from the database
     */
    public ReferenceDto() {
    }

    public ReferenceDto(String url, Timestamp lastCheckDate) {
        this.url = url;
        this.lastCheckDate = lastCheckDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getLastCheckDate() {
        return lastCheckDate;
    }

    public void setLastCheckDate(Timestamp lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
    }
}
