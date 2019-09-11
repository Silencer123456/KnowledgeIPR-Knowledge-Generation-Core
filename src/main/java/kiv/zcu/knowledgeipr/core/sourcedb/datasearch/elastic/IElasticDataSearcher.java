package kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic;

import kiv.zcu.knowledgeipr.core.model.search.Query;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IDataSearcher;

public interface IElasticDataSearcher extends IDataSearcher {


    void searchData(Query query);
}
