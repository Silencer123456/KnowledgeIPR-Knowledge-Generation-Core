package kiv.zcu.knowledgeipr.core;

import com.google.gson.JsonObject;
import com.mongodb.MongoQueryException;
import kiv.zcu.knowledgeipr.rest.StatusResponse;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;

import java.util.List;

/**
 * Handles incoming queries from the REST API.
 * Invokes the data retrieval module to get result set.
 * Invokes report creator to generate a report from the result set.
 * Sends response back to the REST API.
 */
public class ReportManager {

    private ReportCreator reportCreator;
    private DataRetriever dataRetriever;

    public ReportManager(ReportCreator reportCreator) {
        dataRetriever = new DataRetriever();

        this.reportCreator = reportCreator;
    }

    /**
     * Gets the result list from the data retriever and
     * generates a report object from the returned results.
     * @param query - query to process
     * @param page - page number to display
     * @return Response object encapsulating the report.
     */
    public StandardResponse processQuery(Query query, int page, int limit) {
        List<DbRecord> dbRecordList = null;
        try {
            dbRecordList = dataRetriever.runQuery(query, page, limit);
        } catch (MongoQueryException e) {
            e.printStackTrace();

            StandardResponse response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new JsonObject());
            response.setSearchedCount(getCountForDataSource(query.getSourceType()));
            response.setReturnedCount(limit);
        }

        Report report = reportCreator.createReport(dbRecordList);

        StandardResponse response = new StandardResponse(StatusResponse.SUCCESS, "OK", report.getAsJson());
        response.setSearchedCount(getCountForDataSource(query.getSourceType()));
        response.setReturnedCount(limit);

        return response;
    }

    private int getCountForDataSource(String source) {
        int count = 0;
        if (source.equals("publication")) {
            count = 166613546;
        } else if (source.equals("patent")) {
            count = 3645421;
        }

        return count;
    }
}
