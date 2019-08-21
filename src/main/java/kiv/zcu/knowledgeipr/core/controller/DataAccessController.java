package kiv.zcu.knowledgeipr.core.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoQueryException;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.analysis.wordnet.WordNet;
import kiv.zcu.knowledgeipr.core.dataaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.IQueryRunner;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.MongoQueryRunner;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.SearchStrategy;
import kiv.zcu.knowledgeipr.core.report.ChartReport;
import kiv.zcu.knowledgeipr.core.report.DataReport;
import kiv.zcu.knowledgeipr.core.report.FileRepository;
import kiv.zcu.knowledgeipr.core.report.ReportCreator;
import kiv.zcu.knowledgeipr.core.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.search.Search;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.rest.response.ChartResponse;
import kiv.zcu.knowledgeipr.rest.response.StandardResponse;
import kiv.zcu.knowledgeipr.rest.response.StatusResponse;
import kiv.zcu.knowledgeipr.rest.response.WordNetResponse;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class serves as a mediator between the REST API services and data access.
 * The main task of this class is to accept calls from REST API and return generated response,
 * which is sent back to the REST API.
 * The response is generated by first invoking the data retrieval module to get a result set.
 * The report creator is invoked to generate a report from the result set which is then sent back.
 */
public class DataAccessController {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Instantiates reports instances
     */
    private ReportCreator reportCreator;

    /**
     * Provides methods for running concrete queries on the target database
     */
    private IQueryRunner queryRunner;

    //private TextSummarizer summarizer;

    public DataAccessController() {
        reportCreator = new ReportCreator(new FileRepository());
        queryRunner = new MongoQueryRunner();
        //summarizer = new TextSummarizer();
    }

    /**
     *  Initiates the search on the target database according to the search strategy selected.
     *      * Generates a response to be sent back to the client from the returned results.
     * @param searchStrategy - The search strategy to use
     * @param search - The search instance containing relevant search information
     * @param <T> - Type of search according to the search strategy being used
     * @return Response object containing the generated report with results and other info
     */
    public <T extends Search> StandardResponse search(SearchStrategy<T> searchStrategy, T search) {
        StandardResponse response;
        try {
            DataReport report = searchStrategy.search(search);

            response = new StandardResponse(StatusResponse.SUCCESS, "OK", report);
            response.setSearchedCount(getCountForDataSource(search.getQuery().getSourceType()));
            response.setCount(search.getLimit());
            response.setPage(search.getPage());
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

    /**
     * Runs a chart search on the target database. First a cache is checked, if the report is not already generated.
     * In case a cached version is found, it is returned immediately. If the cache does not exist, a search to the
     * target database is constructed and run. The results are collected and a report instance is constructed.
     * Finally a response object is created with the report and returned.
     *
     * @param chartQuery     The search instance which will create the search and retrieve the results
     * @param filename       - The filename under which the final report should be saved, TODO: replace
     * @param collectionName - Name of the collection, on which to run the search (patents or publications)
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

        cachedReport = SerializationUtils.getTreeFromObject(report);

        return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
    }

    public <T, V> ChartResponse chartQuery(ChartQuery<T, V> chartQuery, String filename, DataSourceType collectionName) throws ObjectSerializationException {
        return chartQuery(chartQuery, filename, collectionName, false);
    }

    public void invalidateCache(SearchStrategy searchStrategy) {
        searchStrategy.invalidateCache();
    }

    public WordNetResponse getSynonyms(String word) {
        List<String> synonyms = WordNet.getInstance().getSynonymsForWord(word);
        List<String> hypernyms = WordNet.getInstance().getHypernymsForWord(word);
        return new WordNetResponse(synonyms, hypernyms);
    }

    // TODO: Temp solution
    private int getCountForDataSource(String source) {
        int count = 0;
        if (source.equals("publication")) {
            count = 166613546;
        } else if (source.equals("patent")) {
            count = 3645421;
        }

        return count;
    }

    public IQueryRunner getQueryRunner() {
        return queryRunner;
    }
}
