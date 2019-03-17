package kiv.zcu.knowledgeipr.core;

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
    public StandardResponse processQuery(Query query, int page) {
        // TODO: nejdriv se dotazat do databaze, jestli dotaz uz byl polozen
        final int limit = 10;
        List<DbRecord> dbRecordList = dataRetriever.runQuery(query, page, limit);
        Report report = reportCreator.createReport(dbRecordList);

        // TODO: ulozit dotaz s reportem do db

        StandardResponse response = new StandardResponse(StatusResponse.SUCCESS, "Everything fine", report.getAsJson());
        response.setSearchedCount(getCountForDataSource(query.getSourceType()));
        response.setReturnedCount(limit);

        return response;
    }

    // TODO: Temporary solution
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
