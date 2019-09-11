package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;

import java.util.List;

public interface IElasticDataSearcher extends IDataSearcher {

    List<ElasticRecord> searchData(Query query);
}
