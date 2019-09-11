package kiv.zcu.knowledgeipr.core.model.report;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.MongoRecord;

import java.util.List;

/**
 * Wraps the returned search results along with some additional in.
 * Plus contains summary etc.
 */
public class MongoSearchReport implements SearchReport {
    private String summary = "Data Report";
    private List<MongoRecord> records;

    public MongoSearchReport() {
    }

    public MongoSearchReport(List<MongoRecord> records) {

        this.records = records;
//        for (MongoRecord dbRecord : records) {
        // TODO: make generic to patents or publications...
//            PatentSearchResult searchResult = new PatentSearchResult();
//
//            String json = dbRecord.getDocument().toJson();
//            try {
//                searchResult = new ObjectMapper().readValue(json, PatentSearchResult.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String number = dbRecord.getDocument().getString(ResponseField.DOCUMENT_ID.value);
//            String title = dbRecord.getDocument().getString(ResponseField.TITLE.value);
//            String abstractText = dbRecord.getDocument().getString(ResponseField.ABSTRACT.value);
//            String date = dbRecord.getDocument().getString(ResponseField.DATE.value);
//
//            searchResult.setNumber(number);
//            searchResult.setTitle(title);
//            searchResult.setAbstractText(abstractText);
//            searchResult.setDate(date);
//            this.records.add(searchResult);
//        }
    }

    public List<MongoRecord> getRecords() {
        return records;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
