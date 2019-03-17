package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.rest.QueryRestService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class HelloApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public HelloApplication() {
        // Register our hello service
        singletons.add(new QueryRestService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
