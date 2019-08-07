package kiv.zcu.knowledgeipr.core.controller;

import kiv.zcu.knowledgeipr.core.dataaccess.mongo.IDataSearcher;
import kiv.zcu.knowledgeipr.core.query.Search;
import kiv.zcu.knowledgeipr.core.report.DataReport;
import kiv.zcu.knowledgeipr.rest.errorhandling.UserQueryException;

public abstract class SearchStrategy {

    /**
     * Provides methods for searching the target database for data
     */
    protected IDataSearcher dataSearcher;

    public SearchStrategy(IDataSearcher dataSearcher) {
        this.dataSearcher = dataSearcher;
    }

    abstract DataReport search(Search search) throws UserQueryException;
}
