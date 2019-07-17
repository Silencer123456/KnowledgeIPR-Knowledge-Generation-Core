package kiv.zcu.knowledgeipr.rest;

import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.FileRepository;
import kiv.zcu.knowledgeipr.core.query.queries.PatentOwnershipEvolutionQuery;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.report.ReportCreator;
import kiv.zcu.knowledgeipr.core.report.ReportFilename;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.exception.ObjectSerializationException;
import kiv.zcu.knowledgeipr.rest.response.ChartResponse;
import kiv.zcu.knowledgeipr.rest.response.SimpleResponse;

import javax.ws.rs.*;

@Path("/stats/")
public class StatsRestService {

    private ReportController reportGenerator = new ReportController(new ReportCreator(new FileRepository()));

    @GET
    @Path("/activeAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPatents() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_OWNERS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_FOS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_PUBLISHER);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_VENUES);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_KEYWORD);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ObjectSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_YEAR);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPatents() throws ObjectSerializationException {
        SimpleResponse response = reportGenerator.getCountAuthors(DataSourceType.PATENT.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPublications() throws ObjectSerializationException {
        SimpleResponse response = reportGenerator.getCountAuthors(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/patentOwnershipEvolution/{owner}/{category}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getPatentOwnershipEvolution(@PathParam("owner") String ownersName,
                                                                 @PathParam("category") String category)
            throws ObjectSerializationException {

        // TODO: check category valid

        ChartResponse response = reportGenerator.chartQuery(
                new PatentOwnershipEvolutionQuery(reportGenerator.getStatsQuery(), ownersName, category),
                ReportFilename.PATENT_OWNER_EVO, DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Path("/generateStats/{overwrite}")
    public javax.ws.rs.core.Response generateStats(@PathParam("overwrite") boolean overwrite) {
        reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_AUTHORS, overwrite);
        reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_OWNERS, overwrite);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.ACTIVE_AUTHORS, overwrite);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_KEYWORD, overwrite);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_VENUES, overwrite);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_LANG, overwrite);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_PUBLISHER, overwrite);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_YEAR, overwrite);
        reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_FOS, overwrite);
        //reportGenerator.getCountAuthors(DataSourceType.PATENT.value);
        //reportGenerator.getCountAuthors(DataSourceType.PUBLICATION.value);

        return javax.ws.rs.core.Response.ok().build();
    }
}
