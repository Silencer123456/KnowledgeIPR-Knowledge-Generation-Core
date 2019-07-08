package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.core.mongo.DataRetriever;
import kiv.zcu.knowledgeipr.core.mongo.DbRecord;
import kiv.zcu.knowledgeipr.core.mongo.MongoConnection;
import kiv.zcu.knowledgeipr.core.mongo.StatsRetriever;
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

        } catch (MongoQueryException | UserQueryException e) {
            e.printStackTrace();
            response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new JsonObject());
        } catch (MongoExecutionTimeoutException e) {
            response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new JsonObject());
        }

        return response;
    }

    /**
     * Returns the most active authors
     *
     * @param collectionName Name of the collection in which to search
     * @return Response containing chart data for visualization
     */
    public ChartResponse getActiveAuthors(String collectionName) {
        String reportName = "activeAuthors.json";

        StatsRetriever statsQuery = new StatsRetriever(MongoConnection.getInstance());

        JsonNode cachedReport = reportCreator.loadReportToJson(collectionName + "\\" + reportName);
        if (cachedReport == null) {
            LOGGER.info("Cached report could not be found, querying database");
            // The cached file could not be loaded, we need to fetch new results from the database
            List<Pair<String, Integer>> activeAuthors = statsQuery.activeAuthors(collectionName);

            GraphReport<String, Integer> report = reportCreator.createChartReport("Active Authors", "Authors", "Number of published works", activeAuthors);
            report.save(collectionName + "\\" + reportName);

            cachedReport = report.getAsJson();
        }

        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
    }

    /**
     * TODO: Refactor, make common method for all chart queries
     *
     * @param collectionName
     * @return
     */
    public ChartResponse getCountByFos(String collectionName) {
        String reportName = "countByFos.json";

        StatsRetriever statsQuery = new StatsRetriever(MongoConnection.getInstance());

        JsonNode cachedReport = reportCreator.loadReportToJson(collectionName + "\\" + reportName);
        if (cachedReport == null) {
            LOGGER.info("Cached report could not be found, querying database");
            // The cached file could not be loaded, we need to fetch new results from the database
            List<Pair<String, Integer>> countByFos = statsQuery.countByFos(collectionName);

            GraphReport<String, Integer> report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByFos);
            report.save(collectionName + "\\" + reportName);

            cachedReport = report.getAsJson();
        }

        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
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
