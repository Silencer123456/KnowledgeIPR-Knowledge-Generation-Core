package kiv.zcu.knowledgeipr.rest;

import kiv.zcu.knowledgeipr.core.mongo.DataSourceType;
import kiv.zcu.knowledgeipr.core.report.ReportController;
import kiv.zcu.knowledgeipr.core.report.ReportCreator;
import kiv.zcu.knowledgeipr.core.report.ReportFilename;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.exception.ResponseSerializationException;
import kiv.zcu.knowledgeipr.rest.response.ChartResponse;
import kiv.zcu.knowledgeipr.rest.response.SimpleResponse;

import javax.ws.rs.*;

@Path("/stats/")
public class StatsRestService {

    private ReportController reportGenerator = new ReportController(new ReportCreator());

    @GET
    @Path("/activeAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPatents() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PATENT.value, ReportFilename.ACTIVE_OWNERS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.ACTIVE_AUTHORS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_FOS);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_PUBLISHER);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_VENUES);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_KEYWORD);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ResponseSerializationException {
        ChartResponse response = reportGenerator.chartQuery(DataSourceType.PUBLICATION.value, ReportFilename.COUNT_BY_YEAR);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPatents() throws ResponseSerializationException {
        SimpleResponse response = reportGenerator.getCountAuthors(DataSourceType.PATENT.value);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountAuthorsPublications() throws ResponseSerializationException {
        SimpleResponse response = reportGenerator.getCountAuthors(DataSourceType.PUBLICATION.value);

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
