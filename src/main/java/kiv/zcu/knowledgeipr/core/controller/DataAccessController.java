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
import kiv.zcu.knowledgeipr.api.response.*;
import kiv.zcu.knowledgeipr.core.model.report.ChartReport;
import kiv.zcu.knowledgeipr.core.model.report.EmptySearchReport;
import kiv.zcu.knowledgeipr.core.model.report.ReportHandler;
import kiv.zcu.knowledgeipr.core.model.report.SearchReport;
import kiv.zcu.knowledgeipr.core.model.search.ChartQuery;
import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class serves as a mediator between the REST API services and data access.
 * The main task of this class is to accept calls from REST API and return generated response,
 * which is sent back to the REST API. The class is also responsible for handling all exceptions that
 * happened during execution of queries.
 * The response is generated by first invoking the data retrieval module to get a result set.
 * The report creator is invoked to generate a report from the result set which is then sent back.
 */
public class DataAccessController {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Instantiates reports instances
     */
    private ReportHandler reportHandler;

    private TextSummarizer summarizer;

    public DataAccessController(ReportHandler reportHandler) {
        this.reportHandler = reportHandler;
        summarizer = new TextSummarizer();
    }

    /**
     *  Initiates the search on the target database according to the search strategy selected.
     *  Generates a response to be sent back to the client from the returned results.
     * @param searchStrategy - The search strategy to use
     * @param searchSpecification - The search specification containing search information and how it should be transofrmed
     *                            to the target database query.
     * @param <T> - Type of search according to the search strategy being used
     * @return Response object containing the generated report with results and other info
     */
    public <T extends Search, V extends IDataSearcher> SearchResponse search(SearchStrategy<T, V> searchStrategy, SearchSpecification<T> searchSpecification) {
        SearchResponse response;
        try {
            SearchReport report = searchStrategy.search(searchSpecification);

            Search search = searchSpecification.getSearch();
            response = new SearchResponse(ResponseStatus.SUCCESS, "OK", report);
            response.setDocsInCollection(getCountForDataSource(search.getDataSourceType()));
            response.setSearchedType(search.getDataSourceType());

            response.setDocsReturned(report.getData().size());
            response.setPage(search.getPage());
            //report.setSummary(summarizer.summarizeTextMongo(report.getRecords()).toString());

        } catch (UserQueryException | QueryExecutionException e) {
            LOGGER.warning("Query processing was prematurely terminated: " + e.getMessage());
            response = new SearchResponse(ResponseStatus.ERROR, e.getMessage(), new EmptySearchReport());
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
    public <T, V> IResponse chartQuery(ChartQuery<T, V> chartQuery, String filename, DataSourceType collectionName, boolean overwrite)
            throws ObjectSerializationException {
        JsonNode cachedReport = reportHandler.loadReportToJsonFromFile(collectionName + "\\" + filename);
        if (cachedReport != null && !overwrite) {
            LOGGER.info("Cached report found for " + filename);
            return new ChartResponse(ResponseStatus.SUCCESS, "OK", cachedReport);
        }

        LOGGER.info("Querying " + collectionName + " for: " + chartQuery.getTitle());

        try {
            List<Pair<T, V>> list = chartQuery.get();
            ChartReport<T, V> report = reportHandler.createChartReport(chartQuery.getTitle(), chartQuery.getxLabel(), chartQuery.getyLabel(), list);
            report.save(collectionName + "\\" + filename);
            cachedReport = SerializationUtils.getTreeFromObject(report);

            return new ChartResponse(ResponseStatus.SUCCESS, "OK", cachedReport);
        } catch (QueryExecutionException e) {
            return new SearchResponse(ResponseStatus.ERROR, e.getMessage(), new EmptySearchReport());

        }

    }

    public <T, V> IResponse chartQuery(ChartQuery<T, V> chartQuery, String filename, DataSourceType collectionName) throws ObjectSerializationException {
        return chartQuery(chartQuery, filename, collectionName, false);
    }

    public void invalidateCache(SearchStrategy searchStrategy) {
        searchStrategy.invalidateCache();
    }

    /**
     * Todo: move somewhere else with other similar methods
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

    /**
     * Generates a Wordnet responseof Analyzed words, based on the specified analysis type
     *
     * @param word         - Word for which to create the analysis
     * @param analysisType - The type of analysis
     * @return - List of analyzed words, if incorrect type of analysis is specified, return empty list
     */
    public WordNetResponse wordAnalysis(String word, AnalysisType analysisType) {
        List<AnalyzedWord> list = new ArrayList<>();

        switch (analysisType) {
            case SYNONYM:
                list = WordNet.getInstance().getSynonymsForWord(word);
                break;
            case ANTONYM:
                list = WordNet.getInstance().getAntonymsForWord(word);
                break;
            case HYPERNYM:
                list = WordNet.getInstance().getHypernymsForWord(word);
                break;
            case HYPONYM:
                list = WordNet.getInstance().getHyponymsForWord(word);
                break;
        }

        return new WordNetResponse(list, analysisType);
    }

    // TODO: Temp solution
    private int getCountForDataSource(DataSourceType source) {
        int count = 0;

        int publicationCount = 166613546;
        int patentCount = 103242624;

        switch (source) {
            case PUBLICATION:
                count = publicationCount;
                break;
            case PATENT:
                count = patentCount;
                break;
            case ALL:
                count = publicationCount + patentCount;
        }

        return count;
    }
}
