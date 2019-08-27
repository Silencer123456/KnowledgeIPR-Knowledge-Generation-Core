package kiv.zcu.knowledgeipr.core.report;

import kiv.zcu.knowledgeipr.core.dataaccess.mongo.MongoRecord;

import java.util.List;

/**
 * Wraps the returned search results along with some additional in.
 * Plus contains summary etc.
 */
public class SearchReport {
    private String summary = "Data Report";
    private List<MongoRecord> records;

    public SearchReport() {
    }

    public SearchReport(List<MongoRecord> records) {
        for (MongoRecord record : records) {
            //TODO: Removes the id field from the document so it is not returned back to the user. !!! TMP solution
            record.getDocument().remove("_id");
        }

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
}
