package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;

public interface IElasticDataSearcher extends IDataSearcher<ElasticRecord> {

    DbElasticReport searchData(final Search search);
}
