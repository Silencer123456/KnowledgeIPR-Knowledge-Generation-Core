package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.dataaccess.mongo.MongoDataSearcher;
import kiv.zcu.knowledgeipr.logging.MyLogger;
import kiv.zcu.knowledgeipr.rest.errorhandling.ApiExceptionHandler;
import kiv.zcu.knowledgeipr.rest.errorhandling.GenericExceptionHandler;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationExceptionHandler;
import kiv.zcu.knowledgeipr.rest.services.CategoryRestService;
import kiv.zcu.knowledgeipr.rest.services.DataRestService;
import kiv.zcu.knowledgeipr.rest.services.SearchRestService;
import kiv.zcu.knowledgeipr.rest.services.StatsRestService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class AppRunner extends Application {

    private Set<Object> singletons = new HashSet<>();

    /**
     * Registers services and sets up logger
     */
    public AppRunner() {

        DataAccessController reportGenerator = new DataAccessController(new MongoDataSearcher());

        singletons.add(new SearchRestService(reportGenerator));
        singletons.add(new StatsRestService(reportGenerator));
        singletons.add(new CategoryRestService(reportGenerator));
        singletons.add(new DataRestService(reportGenerator));
        singletons.add(new ApiExceptionHandler());
        singletons.add(new ObjectSerializationExceptionHandler());
        //singletons.add(new MongoExceptionHandler());
        singletons.add(new GenericExceptionHandler());

        MyLogger.setup("restServer");
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
