package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.*;
import kiv.zcu.knowledgeipr.core.database.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.search.CategorySearch;
import kiv.zcu.knowledgeipr.logging.MyLogger;
import kiv.zcu.knowledgeipr.rest.errorhandling.ApiExceptionHandler;
import kiv.zcu.knowledgeipr.rest.errorhandling.GenericExceptionHandler;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationExceptionHandler;
import kiv.zcu.knowledgeipr.rest.filter.RequestLoggingFilter;
import kiv.zcu.knowledgeipr.rest.services.CategoryRestService;
import kiv.zcu.knowledgeipr.rest.services.DataRestService;
import kiv.zcu.knowledgeipr.rest.services.SearchRestService;
import kiv.zcu.knowledgeipr.rest.services.StatsRestService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Entry point of the application
 */
public class AppRunner extends Application {

    private Set<Object> singletons = new HashSet<>();

    /**
     * Registers services and sets up logger.
     * Registers dependencies between classes.
     */
    public AppRunner() {

        IMongoDataSearcher dataSearcher = new MongoDataSearcher();

        DbQueryService dbQueryService = new DbQueryService();

        SearchStrategy<CategorySearch, IMongoDataSearcher> categorySearchStrategy = new CategorySearchStrategy(dataSearcher, dbQueryService);
        SearchStrategy defaultSearchStrategy = new DefaultSearchStrategy(dataSearcher, dbQueryService);

        DataAccessController reportGenerator = new DataAccessController();

        singletons.add(new SearchRestService(reportGenerator, defaultSearchStrategy));
        singletons.add(new StatsRestService(reportGenerator));
        singletons.add(new CategoryRestService(reportGenerator, categorySearchStrategy));
        singletons.add(new DataRestService(reportGenerator));
        singletons.add(new ApiExceptionHandler());
        singletons.add(new ObjectSerializationExceptionHandler());
        singletons.add(new GenericExceptionHandler());
        singletons.add(new RequestLoggingFilter());

        MyLogger.setup("restServer");
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
