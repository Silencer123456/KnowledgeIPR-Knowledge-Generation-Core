package kiv.zcu.knowledgeipr.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.analysis.summarizer.TextSummarizer;
import kiv.zcu.knowledgeipr.analysis.wordnet.WordNet;
import kiv.zcu.knowledgeipr.core.database.repository.DbQueryService;
import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.DbRecord;
import kiv.zcu.knowledgeipr.core.dbaccess.mongo.*;
import kiv.zcu.knowledgeipr.core.query.ChartQuery;
import kiv.zcu.knowledgeipr.core.query.Query;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.rest.response.*;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles incoming chartquery from the REST API.
 * Invokes the data retrieval module to get result set.
 * Invokes report creator to generate a report from the result set.
 * Sends response back to the REST API.
 */
public class ReportController {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private ReportCreator reportCreator;
    private MongoDataRetriever dataRetriever;

    private IQueryRunner statsQuery;

    private DbQueryService dbQueryService;

    private WordNet wordNet;
    private TextSummarizer summarizer;

    public ReportController(ReportCreator reportCreator) {
        this.reportCreator = reportCreator;

        CommonMongoRunner mongoRunner = new CommonMongoRunner(MongoConnection.getInstance());

        dataRetriever = new MongoDataRetriever(mongoRunner);
        statsQuery = new MongoQueryRunner(mongoRunner);

        wordNet = new WordNet();
        summarizer = new TextSummarizer();
        dbQueryService = new DbQueryService();
    }

    /**
     * Gets the result list from the data retriever and
     * generates a report object from the returned results.
     *
     * @param query - query to process
     * @param page  - page number to display
     * @return BaseResponse object encapsulating the report.
     */
    // TODO: Refactor
    public StandardResponse runSearch(Query query, int page, int limit, boolean advanced) {
        StandardResponse response;
        try {
            DataReport report;
            report = dbQueryService.getReportForQuery(query, page, limit);
            if (report == null) {
                List<DbRecord> dbRecordList;
                if (advanced) {
                    dbRecordList = dataRetriever.runSearchAdvanced(query, page, limit);
                } else {
                    dbRecordList = dataRetriever.runSearchSimple(query, page, limit);
                }
                report = reportCreator.createReport(dbRecordList);

                dbQueryService.saveQuery(query, report, limit, page);
            }

            response = new StandardResponse(StatusResponse.SUCCESS, "OK", report);
            response.setSearchedCount(getCountForDataSource(query.getSourceType()));
            response.setCount(limit);
            response.setPage(page);
            //response.setSummary(summarizer.summarizeTextMongo(dbRecordList).toString());

        } catch (MongoQueryException | UserQueryException e) {
            e.printStackTrace();
            response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new DataReport(Collections.emptyList()));
            LOGGER.warning("Query processing was prematurely terminated: " + e.getMessage());
        } catch (MongoExecutionTimeoutException e) {
            response = new StandardResponse(StatusResponse.ERROR, e.getMessage(), new DataReport(Collections.emptyList()));
            LOGGER.info(e.getMessage());
        }

        return response;
    }

    public <T, V> ChartResponse chartQuery(ChartQuery<T, V> chartQuery, String filename, DataSourceType collectionName) throws ObjectSerializationException {
        return chartQuery(chartQuery, filename, collectionName, false);
    }

    /**
     * @param chartQuery     The query instance which will create the query and retrieve the results
     * @param filename       - The filename under which the final report should be saved, TODO: replace
     * @param collectionName - Name of the collection, on which to run the query (patents or publications)
     * @param overwrite      - Flag specifying whether to overwrite the file TODO: change
     * @param <T>            - The X value data type on the result chart
     * @param <V>            - The Y value data type on the result chart
     * @return - The chart response object
     * @throws ObjectSerializationException in case of serialization errors
     */
    public <T, V> ChartResponse chartQuery(ChartQuery<T, V> chartQuery, String filename, DataSourceType collectionName, boolean overwrite)
            throws ObjectSerializationException {
        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(collectionName + "\\" + filename);
        if (cachedReport != null && !overwrite) {
            LOGGER.info("Cached report found for " + filename);
            return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
        }

        LOGGER.info("Querying database for: " + chartQuery.getTitle());

        List<Pair<T, V>> list = chartQuery.get();
        ChartReport<T, V> report = reportCreator.createChartReport(chartQuery.getTitle(), chartQuery.getxLabel(), chartQuery.getyLabel(), list);

        report.save(collectionName + "\\" + filename);

        cachedReport = reportCreator.loadReportToJson(report);

        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
    }

//    public ChartResponse chartQuery(DataSourceType collectionName, ReportFilename reportFilename) throws ObjectSerializationException {
//        return chartQuery(collectionName, reportFilename, false);
//    }
//
//    /**
//     * TODO: Refactor
//     * TODO: Later change all functions to use the above chartQuery(ChartQuery<T, V> chartQuery, String title, String x, String y, String filename) method
//     * Creates a report from a chart query
//     *
//     * @param dataSourceType
//     * @param reportFilename
//     * @return
//     */
//    public ChartResponse chartQuery(DataSourceType dataSourceType, ReportFilename reportFilename, boolean overwrite)
//            throws ObjectSerializationException {
//        JsonNode cachedReport = reportCreator.loadReportToJsonFromFile(dataSourceType + "\\" + reportFilename.value);
//        if (cachedReport != null && !overwrite) {
//            return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
//        }
//
//        ChartReport<String, Integer> report;
//
//        switch (reportFilename) {
//            case COUNT_BY_YEAR:
//                List<Pair<String, Integer>> countByYear = statsQuery.countByField(dataSourceType, ResponseField.YEAR);
//                report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByYear);
//                break;
//            case COUNT_BY_FOS:
//                List<Pair<String, Integer>> countByFos = statsQuery.countByArrayField(dataSourceType, ResponseField.FOS);
//                report = reportCreator.createChartReport("Number of documents by field of study", "Field of study", "Number of documents", countByFos);
//                break;
////            case ACTIVE_OWNERS:
////                List<Pair<String, Integer>> activeOwners = statsQuery.activePeople(collectionName, ResponseField.OWNERS.value, 1000);
////                report = reportCreator.createChartReport("Active owners", "Owners", "Number of published works", activeOwners);
////                break;
////            case ACTIVE_AUTHORS:
////                List<Pair<String, Integer>> activeAuthors = statsQuery.activePeople(collectionName, ResponseField.AUTHORS.value, 20);
////                report = reportCreator.createChartReport("Active authors", "Authors", "Number of published works", activeAuthors);
////                break;
//            case COUNT_BY_PUBLISHER:
//                List<Pair<String, Integer>> prolificPublishers = statsQuery.countByField(dataSourceType, ResponseField.PUBLISHER);
//                report = reportCreator.createChartReport("Prolific publishers", "Publisher name", "Number of publications", prolificPublishers);
//                break;
//            case COUNT_BY_KEYWORD:
//                List<Pair<String, Integer>> keywords = statsQuery.countByArrayField(dataSourceType, ResponseField.KEYWORDS);
//                report = reportCreator.createChartReport("Number of documents by keywords", "Keyword", "Number of documents", keywords);
//                break;
//            case COUNT_BY_VENUES:
//                List<Pair<String, Integer>> venues = statsQuery.countByField(dataSourceType, ResponseField.VENUE);
//                report = reportCreator.createChartReport("Number of documents by venues", "Venue", "Number of documents", venues);
//                break;
//            case COUNT_BY_LANG:
//                List<Pair<String, Integer>> countByLang = statsQuery.countByField(dataSourceType, ResponseField.LANG);
//                report = reportCreator.createChartReport("Number of documents by venues", "Venue", "Number of documents", countByLang);
//                break;
//            default:
//                report = reportCreator.createChartReport("", "", "", new ArrayList<>());
//
//        }
//
//        report.save(dataSourceType + "\\" + reportFilename.value);
//
//        cachedReport = reportCreator.loadReportToJson(report);
//
//        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
//    }

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
