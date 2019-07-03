package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.mongo.DataRetriever;
import kiv.zcu.knowledgeipr.core.mongo.DbRecord;
import kiv.zcu.knowledgeipr.core.mongo.MongoConnection;
import kiv.zcu.knowledgeipr.core.mongo.StatsQuery;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.rest.StatusResponse;
import kiv.zcu.knowledgeipr.rest.exception.UserQueryException;
import kiv.zcu.knowledgeipr.rest.response.ChartResponse;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
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
        String reportName = "test.json";

        StatsQuery statsQuery = new StatsQuery(MongoConnection.getInstance());

//        List<Pair<String, Integer>> activeAuthors = new ArrayList<>();
//        activeAuthors.add(new Pair<>("Test1", 123));
//        activeAuthors.add(new Pair<>("Test2", 324));
//        activeAuthors.add(new Pair<>("Test3", 1324));
//        activeAuthors.add(new Pair<>("Test4", 12234));

        // TODO: Move somewhere else
        JsonNode cachedReport = loadReportToJson(reportName);
        if (cachedReport == null) {
            LOGGER.info("Cached report could not be found, querying database");
            // The cached file could not be loaded, we need to fetch new results from the database
            List<Pair<String, Integer>> activeAuthors = statsQuery.activeAuthors();
            GraphReport<String, Integer> report = reportCreator.createChartReport("Active Authors", "Authors", "Number of publications", activeAuthors);
            report.save(reportName);

            cachedReport = report.getAsJson();
        }

        ChartResponse response = new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);

        return response;
    }

    // TODO: Refactor
    private JsonNode loadReportToJson(String filename) {
        try {
            Properties properties = AppServletContextListener.getProperties();
            String basePath = properties.getProperty("reports");

            String content = new String(Files.readAllBytes(Paths.get(basePath + filename)));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(content);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
