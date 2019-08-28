package kiv.zcu.knowledgeipr.core.model.search;

import kiv.zcu.knowledgeipr.core.domain.Person;

import java.util.List;

/**
 * Represents a single document from the database.
 */
public class PatentSearchResult implements ISearchResult {

    private String number;
    private String title;
    private String abstractText;
    private int year;
    private String date;
    private List<Person> authorsList;
    private List<Person> ownersList;
    private String dataSource;
    private String lang;
    private String status;
    private String country;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Person> getAuthorsList() {
        return authorsList;
    }

    public void setAuthorsList(List<Person> authorsList) {
        this.authorsList = authorsList;
    }

    public List<Person> getOwnersList() {
        return ownersList;
    }

    public void setOwnersList(List<Person> ownersList) {
        this.ownersList = ownersList;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
