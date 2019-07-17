package kiv.zcu.knowledgeipr.app;

import kiv.zcu.knowledgeipr.core.dbaccess.FileRepository;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.report.ReportCreator;
import kiv.zcu.knowledgeipr.logging.MyLogger;
import kiv.zcu.knowledgeipr.rest.CategoryRestService;
import kiv.zcu.knowledgeipr.rest.DataRestService;
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

        ReportController reportGenerator = new ReportController(new ReportCreator(new FileRepository()));

        singletons.add(new QueryRestService(reportGenerator));
        singletons.add(new StatsRestService(reportGenerator));
        singletons.add(new CategoryRestService(reportGenerator));
        singletons.add(new DataRestService(reportGenerator));
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
