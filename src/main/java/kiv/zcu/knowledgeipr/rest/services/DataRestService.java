package kiv.zcu.knowledgeipr.rest.services;

import kiv.zcu.knowledgeipr.core.report.ReportController;

import javax.ws.rs.Path;

@Path("/data/")
public class DataRestService {

    private ReportController reportController;

    public DataRestService(ReportController reportController) {
        this.reportController = reportController;
    }

}
