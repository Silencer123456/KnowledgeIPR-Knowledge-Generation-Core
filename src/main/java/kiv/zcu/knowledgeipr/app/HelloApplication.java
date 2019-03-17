package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.rest.QueryRestService;
import kiv.zcu.knowledgeipr.rest.exception.ApiExceptionHandler;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class HelloApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public HelloApplication() {
        // Register our hello service
        singletons.add(new QueryRestService());
        singletons.add(new ApiExceptionHandler());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
