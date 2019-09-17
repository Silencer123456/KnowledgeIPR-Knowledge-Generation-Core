package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchSpecification;

public interface IElasticDataSearcher extends IDataSearcher<ElasticRecord> {

    DbElasticReport search(SearchSpecification searchSpecification);
}
