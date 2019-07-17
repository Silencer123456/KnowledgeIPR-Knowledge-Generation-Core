package kiv.zcu.knowledgeipr.rest;

import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.query.queries.PatentOwnershipEvolutionQuery;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.report.ReportFilename;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.response.ChartResponse;
import kiv.zcu.knowledgeipr.rest.response.SimpleResponse;

import javax.ws.rs.*;

@Path("/stats/")
public class StatsRestService {

    private ReportController reportController;

    public StatsRestService(ReportController reportController) {
        this.reportController = reportController;
    }

    @GET
    @Path("/activeAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPatents() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PATENT, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PATENT, ReportFilename.ACTIVE_OWNERS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_FOS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_PUBLISHER);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_VENUES);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_KEYWORD);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPatents() throws ObjectSerializationException {
        SimpleResponse response = reportController.getCountAuthors(DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPublications() throws ObjectSerializationException {
        SimpleResponse response = reportController.getCountAuthors(DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/patentOwnershipEvolution/{owner}/{category}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getPatentOwnershipEvolution(@PathParam("owner") String ownersName,
                                                                 @PathParam("category") String category)
            throws ObjectSerializationException {

        // TODO! check category valid
        // TODO! better way of disambiguaiting reports, probably saving to database instead

        ChartResponse response = reportController.chartQuery(
                new PatentOwnershipEvolutionQuery(reportController.getStatsQuery(), ownersName, category),
                "ownerEvol" + ownersName + category + ".json", DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Path("/generateStats/{overwrite}")
    public javax.ws.rs.core.Response generateStats(@PathParam("overwrite") boolean overwrite) throws ObjectSerializationException {
        reportController.chartQuery(DataSourceType.PATENT, ReportFilename.ACTIVE_AUTHORS, overwrite);
        reportController.chartQuery(DataSourceType.PATENT, ReportFilename.ACTIVE_OWNERS, overwrite);
        reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.ACTIVE_AUTHORS, overwrite);
        reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_KEYWORD, overwrite);
        reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_VENUES, overwrite);
        reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_LANG, overwrite);
        reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_PUBLISHER, overwrite);
        reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR, overwrite);
        reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_FOS, overwrite);
        //reportController.getCountAuthors(DataSourceType.PATENT.value);
        //reportController.getCountAuthors(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().build();
    }
}
