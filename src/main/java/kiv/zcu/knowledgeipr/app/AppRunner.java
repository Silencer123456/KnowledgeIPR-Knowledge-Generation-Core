package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.logging.MyLogger;
import kiv.zcu.knowledgeipr.rest.CategoryRestService;
import kiv.zcu.knowledgeipr.rest.QueryRestService;
import kiv.zcu.knowledgeipr.rest.StatsRestService;
import kiv.zcu.knowledgeipr.rest.errorhandling.ApiExceptionHandler;
import kiv.zcu.knowledgeipr.rest.errorhandling.GenericExceptionHandler;
import kiv.zcu.knowledgeipr.rest.errorhandling.MongoExceptionHandler;
import kiv.zcu.knowledgeipr.rest.errorhandling.ResponseSerializationExceptionHandler;

import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AppRunner extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    /**
     * Registers services and sets up logger
     */
    public AppRunner() {
        singletons.add(new QueryRestService());
        singletons.add(new StatsRestService());
        singletons.add(new CategoryRestService());
        singletons.add(new ApiExceptionHandler());
        singletons.add(new ResponseSerializationExceptionHandler());
        singletons.add(new MongoExceptionHandler());
        singletons.add(new GenericExceptionHandler());

        try {
            MyLogger.setup("restServer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
