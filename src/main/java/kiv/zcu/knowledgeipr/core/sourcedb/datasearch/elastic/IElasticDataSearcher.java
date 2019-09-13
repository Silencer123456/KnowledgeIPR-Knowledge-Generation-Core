package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Search;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;

import java.util.List;

public interface IElasticDataSearcher extends IDataSearcher<ElasticRecord> {

    List<ElasticRecord> searchData(final Search search);
}
