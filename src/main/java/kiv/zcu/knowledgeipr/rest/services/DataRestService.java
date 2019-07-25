package kiv.zcu.knowledgeipr.rest.services;

import kiv.zcu.knowledgeipr.core.controller.DataAccessController;

import javax.ws.rs.Path;

@Path("/data/")
public class DataRestService {

    private DataAccessController dataAccessController;

    public DataRestService(DataAccessController dataAccessController) {
        this.dataAccessController = dataAccessController;
    }

}
