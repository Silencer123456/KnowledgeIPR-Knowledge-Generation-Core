package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.api.errorhandling.ApiExceptionHandler;
import kiv.zcu.knowledgeipr.api.errorhandling.GenericExceptionHandler;
import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationExceptionHandler;
import kiv.zcu.knowledgeipr.api.filter.RequestLoggingFilter;
import kiv.zcu.knowledgeipr.api.services.CategoryRestService;
import kiv.zcu.knowledgeipr.api.services.DataRestService;
import kiv.zcu.knowledgeipr.api.services.SearchRestService;
import kiv.zcu.knowledgeipr.api.services.StatsRestService;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.knowledgedb.service.DbQueryService;
import kiv.zcu.knowledgeipr.core.model.report.FileRepository;
import kiv.zcu.knowledgeipr.core.model.report.ReportHandler;
import kiv.zcu.knowledgeipr.core.model.search.CategorySearch;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.SearchStrategy;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.*;
import kiv.zcu.knowledgeipr.logging.MyLogger;

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

        DataAccessController reportGenerator = new DataAccessController(new MongoQueryRunner(), new ReportHandler(new FileRepository()));

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
