package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.analysis.summarizer.TextSummarizer;
import kiv.zcu.knowledgeipr.analysis.wordnet.WordNet;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.DbRecord;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;
import kiv.zcu.knowledgeipr.core.dbaccess.mongo.*;
import kiv.zcu.knowledgeipr.core.query.ChartQuery;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.rest.response.*;

import java.util.ArrayList;
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

    private IQueryRunner statsQuery;

    private WordNet wordNet;
    private TextSummarizer summarizer;

    public ReportController(ReportCreator reportCreator) {
        this.reportCreator = reportCreator;

        CommonMongoRunner mongoRunner = new CommonMongoRunner(MongoConnection.getInstance());

        dataRetriever = new DataRetriever(mongoRunner);
        statsQuery = new MongoQueryRunner(mongoRunner);

        wordNet = new WordNet();
        summarizer = new TextSummarizer();
    }

    /**
     * Gets the result list from the data retriever and
     * generates a report object from the returned results.
     *
     * @param query - query to process
     * @param page  - page number to display
     * @return BaseResponse object encapsulating the report.
     */
    public StandardResponse runSearch(Query query, int page, int limit, boolean advanced) {
        StandardResponse response;

        List<DbRecord> dbRecordList;
        try {
            if (advanced) {
                dbRecordList = dataRetriever.runSearchAdvanced(query, page, limit);
            } else {
                dbRecordList = dataRetriever.runSearchSimple(query, page, limit);
            }

            DataReport report = reportCreator.createReport(dbRecordList);

            response = new StandardResponse(StatusResponse.SUCCESS, "OK", report.getAsJson());
            response.setSearchedCount(getCountForDataSource(query.getSourceType()));
            response.setReturnedCount(limit);
            response.setPage(page);

            //response.setSummary(summarizer.summarizeTextMongo(dbRecordList).toString());

        } catch (MongoQueryException | UserQueryException e) {
            e.printStackTrace();
            response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new JsonObject());
            LOGGER.info("Query processing was prematurely terminated: " + e.getMessage());
        } catch (MongoExecutionTimeoutException e) {
            response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new JsonObject());
            LOGGER.info(e.getMessage());
        }

        return response;
    }

    public <T, V> ChartResponse chartQuery(ChartQuery<T, V> chartQuery, String filename, DataSourceType collectionName) throws ObjectSerializationException {
        return chartQuery(chartQuery, filename, collectionName, false);
    }

    public <T, V> ChartResponse chartQuery(ChartQuery<T, V> chartQuery, String filename, DataSourceType collectionName, boolean overwrite)
            throws ObjectSerializationException {
        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + filename);
        if (cachedReport != null && !overwrite) {
            return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
        }

        List<Pair<T, V>> list = chartQuery.get();
        ChartReport<T, V> report = reportCreator.createChartReport(chartQuery.getTitle(), chartQuery.getxLabel(), chartQuery.getyLabel(), list);

        report.save(collectionName + "\\" + filename);

        cachedReport = reportCreator.loadReportToJson(report);

        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
    }

    public ChartResponse chartQuery(DataSourceType collectionName, ReportFilename reportFilename) throws ObjectSerializationException {
        return chartQuery(collectionName, reportFilename, false);
    }

    /**
     * TODO: Refactor
     * TODO: Later change all functions to use the above chartQuery(ChartQuery<T, V> chartQuery, String title, String x, String y, String filename) method
     * Creates a report from a chart query
     *
     * @param collectionName
     * @param reportFilename
     * @return
     */
    public ChartResponse chartQuery(DataSourceType collectionName, ReportFilename reportFilename, boolean overwrite)
            throws ObjectSerializationException {
        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + reportFilename.value);
        if (cachedReport != null && !overwrite) {
            return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
        }

        ChartReport<String, Integer> report;

        switch (reportFilename) {
            case COUNT_BY_YEAR:
                List<Pair<String, Integer>> countByYear = statsQuery.countByField(collectionName, ResponseField.YEAR);
                report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByYear);
                break;
            case COUNT_BY_FOS:
                List<Pair<String, Integer>> countByFos = statsQuery.countByField(collectionName, ResponseField.FOS);
                report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByFos);
                break;
            case ACTIVE_OWNERS:
                List<Pair<String, Integer>> activeOwners = statsQuery.activePeople(collectionName, ResponseField.OWNERS.value, 1000);
                report = reportCreator.createChartReport("Active owners", "Owners", "Number of published works", activeOwners);
                break;
            case ACTIVE_AUTHORS:
                List<Pair<String, Integer>> activeAuthors = statsQuery.activePeople(collectionName, ResponseField.AUTHORS.value, 20);
                report = reportCreator.createChartReport("Active authors", "Authors", "Number of published works", activeAuthors);
                break;
            case COUNT_BY_PUBLISHER:
                List<Pair<String, Integer>> prolificPublishers = statsQuery.countByField(collectionName, ResponseField.PUBLISHER);
                report = reportCreator.createChartReport("Prolific publishers", "Publisher name", "Number of publications", prolificPublishers);
                break;
            case COUNT_BY_KEYWORD:
                List<Pair<String, Integer>> keywords = statsQuery.countByField(collectionName, ResponseField.KEYWORDS);
                report = reportCreator.createChartReport("Number of documents by keywords", "Keyword", "Number of documents", keywords);
                break;
            case COUNT_BY_VENUES:
                List<Pair<String, Integer>> venues = statsQuery.countByField(collectionName, ResponseField.VENUE);
                report = reportCreator.createChartReport("Number of documents by venues", "Venue", "Number of documents", venues);
                break;
            case COUNT_BY_LANG:
                List<Pair<String, Integer>> countByLang = statsQuery.countByField(collectionName, ResponseField.LANG);
                report = reportCreator.createChartReport("Number of documents by venues", "Venue", "Number of documents", countByLang);
                break;
            default:
                report = reportCreator.createChartReport("", "", "", new ArrayList<>());

        }

        report.save(collectionName + "\\" + reportFilename.value);

        cachedReport = reportCreator.loadReportToJson(report);

        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
    }

    // TODO: probably delete or hardcode
    public SimpleResponse getCountAuthors(DataSourceType collectionName) throws ObjectSerializationException {
        String reportName = "countOfAuthors.json";

        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName.value + "\\" + reportName);
        if (cachedReport == null) {
            LOGGER.info("Cached report could not be found, querying database");
            // The cached file could not be loaded, we need to fetch new results from the database
            //int authorCount = statsQuery.getPeopleCount(collectionName, "authors");

            //int authorCount = 2;
            int authorCount = 120000;

            SimpleReport simpleReport = new SimpleReport(authorCount);
            simpleReport.save(collectionName.value + "\\" + reportName);

            cachedReport = reportCreator.loadReportToJson(simpleReport);
        }

        return new SimpleResponse(cachedReport);
    }

    public WordNetResponse getSynonyms(String word) {
        List<String> synonyms = wordNet.getSynonymsForWord(word);
        List<String> hypernyms = wordNet.getHypernymsForWord(word);
        return new WordNetResponse(synonyms, hypernyms);
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

    public IQueryRunner getStatsQuery() {
        return statsQuery;
    }
}
