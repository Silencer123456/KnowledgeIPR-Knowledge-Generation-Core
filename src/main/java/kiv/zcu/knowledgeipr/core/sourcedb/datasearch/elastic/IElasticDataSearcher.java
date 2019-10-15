package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.api.errorhandling.QueryExecutionException;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;

/**
 * Contains methods for searching on the ElasticSearch database.
 */
public interface IElasticDataSearcher extends IDataSearcher<ElasticRecord> {

    /**
     * Searches the target database from a provided specification
     *
     * @param searchSpecification - Contains information related to the concrete search
     * @return A report used for ElasticSearch
     */
    DbElasticReportWrapper search(SearchSpecification searchSpecification) throws QueryExecutionException;
}
