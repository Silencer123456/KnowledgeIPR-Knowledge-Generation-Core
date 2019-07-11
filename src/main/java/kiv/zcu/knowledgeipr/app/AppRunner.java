package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.logging.MyLogger;
import kiv.zcu.knowledgeipr.rest.QueryRestService;
import kiv.zcu.knowledgeipr.rest.exception.ApiExceptionHandler;
import kiv.zcu.knowledgeipr.rest.exception.MongoExceptionHandler;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationExceptionHandler;

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
        singletons.add(new ApiExceptionHandler());
        singletons.add(new ResponseSerializationExceptionHandler());
        singletons.add(new MongoExceptionHandler());

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
