package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.analysis.wordnet.WordNet;
import kiv.zcu.knowledgeipr.core.mongo.*;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.rest.exception.UserQueryException;
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

    private StatsRetriever statsQuery;

    private WordNet wordNet;

    public ReportController(ReportCreator reportCreator) {
        this.reportCreator = reportCreator;

        MongoRunner mongoRunner = new MongoRunner(MongoConnection.getInstance());

        dataRetriever = new DataRetriever(mongoRunner);
        statsQuery = new StatsRetriever(mongoRunner);

        wordNet = new WordNet();
    }

    /**
     * Gets the result list from the data retriever and
     * generates a report object from the returned results.
     *
     * @param query - query to process
     * @param page  - page number to display
     * @return Response object encapsulating the report.
     */
    public StandardResponse processQuery(Query query, int page, int limit) {
        StandardResponse response;

        List<DbRecord> dbRecordList;
        try {
            dbRecordList = dataRetriever.runQuery(query, page, limit);

            DataReport report = reportCreator.createReport(dbRecordList);

            response = new StandardResponse(StatusResponse.SUCCESS, "OK", report.getAsJson());
            response.setSearchedCount(getCountForDataSource(query.getSourceType()));
            response.setReturnedCount(limit);
            response.setPage(page);

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

    /**
     * TODO: Refactor further
     * Creates a report from a chart query
     *
     * @param collectionName
     * @param reportFilename
     * @return
     */
    public ChartResponse chartQuery(String collectionName, ReportFilename reportFilename) {
        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + reportFilename.value);
        if (cachedReport != null) {
            return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
        }

        GraphReport<String, Integer> report;

        switch (reportFilename) {
            case COUNT_BY_YEAR:
                List<Pair<String, Integer>> countByYear = statsQuery.countByYear(collectionName);
                report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByYear);
                break;
            case COUNT_BY_FOS:
                List<Pair<String, Integer>> countByFos = statsQuery.countByFos(collectionName);
                report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByFos);
                break;
            case ACTIVE_OWNERS:
                List<Pair<String, Integer>> activeOwners = statsQuery.activePeople(collectionName, "owners");
                report = reportCreator.createChartReport("Active owners", "Owners", "Number of published works", activeOwners);
                break;
            case ACTIVE_AUTHORS:
                List<Pair<String, Integer>> activeAuthors = statsQuery.activePeople(collectionName, "authors");

                report = reportCreator.createChartReport("Active authors", "Authors", "Number of published works", activeAuthors);
                break;
            case COUNT_BY_PUBLISHER:
                List<Pair<String, Integer>> prolificPublishers = statsQuery.prolificPublishers(collectionName);

                report = reportCreator.createChartReport("Prolific publishers", "Publisher name", "Number of publications", prolificPublishers);
                break;
            case COUNT_BY_KEYWORD:
                List<Pair<String, Integer>> keywords = statsQuery.countByKeyword(collectionName);

                report = reportCreator.createChartReport("Number of documents by keywords", "Keyword", "Number of documents", keywords);
                break;
            case COUNT_BY_VENUES:
                List<Pair<String, Integer>> venues = statsQuery.countByVenue(collectionName);

                report = reportCreator.createChartReport("Number of documents by venues", "Venue", "Number of documents", venues);
                break;
            default:
                report = new GraphReport<>("", "", "", new ArrayList<>());
        }

        report.save(collectionName + "\\" + reportFilename.value);

        cachedReport = reportCreator.loadReportToJson(report);

        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
    }

//    /**
//     * Returns the most active authors
//     *
//     * @param collectionName Name of the collection in which to search
//     * @return Response containing chart data for visualization
//     */
//    public ChartResponse getActiveAuthors(String collectionName) {
//        return getActivePeople(collectionName, "authors");
//    }
//
//    public ChartResponse getActiveOwners(String collectionName) {
//        return getActivePeople(collectionName, "owners");
//    }
//
//    /**
//     * Creates a report of the most active people (authors or owners)
//     *
//     * @param collectionName - Collection in which to search
//     * @param peopleType     - authors or owners, specifies the name of the array
//     * @return Created response with graph data
//     */
//    private ChartResponse getActivePeople(String collectionName, String peopleType) {
//        String reportName = "active" + peopleType + ".json";
//
//        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + reportName);
//        if (cachedReport == null) {
//            LOGGER.info("Cached report could not be found, querying database");
//            // The cached file could not be loaded, we need to fetch new results from the database
//            List<Pair<String, Integer>> activePeople = statsQuery.activePeople(collectionName, peopleType);
//
//            GraphReport<String, Integer> report = reportCreator.createChartReport("The most active " + collectionName + " " + peopleType, peopleType, "Number of published works", activePeople);
//            report.save(collectionName + "\\" + reportName);
//
//            cachedReport = reportCreator.loadReportToJson(report);
//        }
//
//        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
//    }
//
//    /**
//     * @param collectionName - Name of the collection
//     * @return
//     */
//    public ChartResponse getCountByFos(String collectionName) {
//        String reportName = "countByFos.json";
//
//        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + reportName);
//        if (cachedReport == null) {
//            LOGGER.info("Cached report could not be found, querying database");
//            // The cached file could not be loaded, we need to fetch new results from the database
//            List<Pair<String, Integer>> countByFos = statsQuery.countByFos(collectionName);
////            List<Pair<String, Integer>> countByFos = new ArrayList<>();
////            countByFos.add(new Pair<>("Asd", 122));
////            countByFos.add(new Pair<>("Asd", 122));
////            countByFos.add(new Pair<>("Asd", 122));
////            countByFos.add(new Pair<>("Asd", 122));
//
//            GraphReport<String, Integer> report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByFos);
//            report.save(collectionName + "\\" + reportName);
//
//            cachedReport = reportCreator.loadReportToJson(report);
//        }
//
//        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
//    }
//
//    public ChartResponse getCountByYear(String collectionName) {
//        String reportName = "countByYear.json";
//
//        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + reportName);
//        if (cachedReport == null) {
//            LOGGER.info("Cached report could not be found, querying database");
//            // The cached file could not be loaded, we need to fetch new results from the database
//            List<Pair<Integer, Integer>> countByFos = statsQuery.countByYear(collectionName);
////            List<Pair<Integer, Integer>> countByFos = new ArrayList<>();
////            countByFos.add(new Pair<>(2011, 2121213));
////            countByFos.add(new Pair<>(2011, 2121213));
////            countByFos.add(new Pair<>(2011, 2121213));
////            countByFos.add(new Pair<>(2011, 2121213));
////            countByFos.add(new Pair<>(2011, 2121213));
//
//            GraphReport<Integer, Integer> report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByFos);
//            report.save(collectionName + "\\" + reportName);
//
//            cachedReport = reportCreator.loadReportToJson(report);
//        }
//
//        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
//    }

    // TODO: probably delete or hardcode
    public SimpleResponse getCountAuthors(String collectionName) {
        String reportName = "countOfAuthors.json";

        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + reportName);
        if (cachedReport == null) {
            LOGGER.info("Cached report could not be found, querying database");
            // The cached file could not be loaded, we need to fetch new results from the database
            int authorCount = statsQuery.getPeopleCount(collectionName, "authors");

            //int authorCount = 2;

            SimpleReport simpleReport = new SimpleReport(authorCount);
            simpleReport.save(collectionName + "\\" + reportName);

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
}
