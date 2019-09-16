package kiv.zcu.knowledgeipr.core.controller;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;
import kiv.zcu.knowledgeipr.analysis.summarizer.TextSummarizer;
import kiv.zcu.knowledgeipr.analysis.wordnet.AnalysisType;
import kiv.zcu.knowledgeipr.analysis.wordnet.AnalyzedWord;
import kiv.zcu.knowledgeipr.analysis.wordnet.WordNet;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.api.errorhandling.UserQueryException;
import kiv.zcu.knowledgeipr.api.response.ChartResponse;
import kiv.zcu.knowledgeipr.api.response.SearchResponse;
import kiv.zcu.knowledgeipr.api.response.StatusResponse;
import kiv.zcu.knowledgeipr.api.response.WordNetResponse;
import kiv.zcu.knowledgeipr.core.model.report.ChartReport;
import kiv.zcu.knowledgeipr.core.model.report.EmptySearchReport;
import kiv.zcu.knowledgeipr.core.model.report.ReportHandler;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.model.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

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
    private ReportHandler reportHandler;

    /**
     * Provides methods for running concrete queries on the target database
     */
    private IQueryRunner queryRunner;

    private TextSummarizer summarizer;

    public DataAccessController(IQueryRunner queryRunner, ReportHandler reportHandler) {
        this.reportHandler = reportHandler;
        this.queryRunner = queryRunner;
        summarizer = new TextSummarizer();
    }

    /**
     *  Initiates the search on the target database according to the search strategy selected.
     *      * Generates a response to be sent back to the client from the returned results.
     * @param searchStrategy - The search strategy to use
     * @param search - The search instance containing relevant search information
     * @param <T> - Type of search according to the search strategy being used
     * @return Response object containing the generated report with results and other info
     */
    public <T extends Search, V extends IDataSearcher> SearchResponse search(SearchStrategy<T, V> searchStrategy, T search) {
        SearchResponse response;
        try {
            SearchReport report = searchStrategy.search(search);

            response = new SearchResponse(StatusResponse.SUCCESS, "OK", report);
            response.setDocsInCollection(getCountForDataSource(search.getDataSourceType()));
            response.setSearchedCollection(search.getDataSourceType());

            response.setDocsReturned(report.getData().size());
            response.setPage(search.getPage());
            //report.setSummary(summarizer.summarizeTextMongo(report.getRecords()).toString());

        } catch (UserQueryException | QueryExecutionException e) {
            LOGGER.warning("Query processing was prematurely terminated: " + e.getMessage());
            response = new SearchResponse(StatusResponse.ERROR, e.getMessage(), new EmptySearchReport());
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
        JsonNode cachedReport = reportHandler.loadReportToJsonFromFile(collectionName + "\\" + filename);
        if (cachedReport != null && !overwrite) {
            LOGGER.info("Cached report found for " + filename);
            return new ChartResponse(StatusResponse.SUCCESS, "OK", cachedReport);
        }

        LOGGER.info("Querying database for: " + chartQuery.getTitle());

        List<Pair<T, V>> list = chartQuery.get();
        ChartReport<T, V> report = reportHandler.createChartReport(chartQuery.getTitle(), chartQuery.getxLabel(), chartQuery.getyLabel(), list);

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

    /**
     * Todo: move somewhere else along with other similar methods
     *
     * @param word
     * @return
     */
    public WordNetResponse getSynonyms(String word) {
        List<AnalyzedWord> synonyms = WordNet.getInstance().getSynonymsForWord(word);
        return new WordNetResponse(synonyms, AnalysisType.SYNONYM);
    }

    public WordNetResponse getAntonyms(String word) {
        List<AnalyzedWord> antonyms = WordNet.getInstance().getAntonymsForWord(word);
        return new WordNetResponse(antonyms, AnalysisType.ANTONYM);
    }

    public WordNetResponse getHypernyms(String word) {
        List<AnalyzedWord> hypernyms = WordNet.getInstance().getHypernymsForWord(word);
        return new WordNetResponse(hypernyms, AnalysisType.HYPERNYM);
    }

    public WordNetResponse getHyponyms(String word) {
        List<AnalyzedWord> hyponyms = WordNet.getInstance().getHyponymsForWord(word);
        return new WordNetResponse(hyponyms, AnalysisType.HYPONYM);
    }

    // TODO: Temp solution
    private int getCountForDataSource(DataSourceType source) {
        int count = 0;

        switch (source) {
            case PUBLICATION:
                count = 166613546;
                break;
            case PATENT:
                count = 3645421;
                break;
        }

        return count;
    }

    public IQueryRunner getQueryRunner() {
        return queryRunner;
    }
}
