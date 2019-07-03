package kiv.zcu.knowledgeipr.core.report;

import com.google.gson.JsonObject;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.DataRetriever;
import kiv.zcu.knowledgeipr.core.DbRecord;
import kiv.zcu.knowledgeipr.core.StatsQuery;
import kiv.zcu.knowledgeipr.core.dbconnection.MongoConnection;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.rest.StatusResponse;
import kiv.zcu.knowledgeipr.rest.exception.UserQueryException;
import kiv.zcu.knowledgeipr.rest.response.ChartResponse;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;

import java.util.List;
import java.util.logging.Logger;

/**
 * Handles incoming queries from the REST API.
 * Invokes the data retrieval module to get result set.
 * Invokes report creator to generate a report from the result set.
 * Sends response back to the REST API.
 */
public class ReportController {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private ReportCreator reportCreator;
    private DataRetriever dataRetriever;

    public ReportController(ReportCreator reportCreator) {
        MongoConnection mongoConnection = MongoConnection.getInstance();
        dataRetriever = new DataRetriever(mongoConnection);

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
        StandardResponse response;

        List<DbRecord> dbRecordList;
        try {
            dbRecordList = dataRetriever.runQuery(query, page, limit);

            Report report = reportCreator.createReport(dbRecordList);

            response = new StandardResponse(StatusResponse.SUCCESS, "OK", report.getAsJson());
            response.setSearchedCount(getCountForDataSource(query.getSourceType()));
            response.setReturnedCount(limit);

        } catch (MongoQueryException | UserQueryException | MongoExecutionTimeoutException e) {
            e.printStackTrace();
            response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new JsonObject());
        }

        return response;
    }

    public ChartResponse getActiveAuthors() {
        StatsQuery statsQuery = new StatsQuery(MongoConnection.getInstance());

        // TODO: before querying mongo, get results from cache (filesystem...)
        List<Pair<String, Integer>> activeAuthors = statsQuery.activeAuthors();
        GraphReport<String, Integer> report = reportCreator.createChartReport("Active Authors", "Authors", "Number of publications", activeAuthors);

        // TODO: Save serialized report to cache(filesystem...)

        ChartResponse response = new ChartResponse(StatusResponse.SUCCESS, "OK", report.getAsJson());

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
