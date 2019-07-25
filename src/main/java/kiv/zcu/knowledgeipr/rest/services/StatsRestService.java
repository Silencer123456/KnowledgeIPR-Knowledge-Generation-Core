package kiv.zcu.knowledgeipr.rest.services;

import kiv.zcu.knowledgeipr.core.dbaccess.DataSourceType;
import kiv.zcu.knowledgeipr.core.dbaccess.ResponseField;
import kiv.zcu.knowledgeipr.core.query.queries.ActivePersonQuery;
import kiv.zcu.knowledgeipr.core.query.queries.CountByArrayFieldQuery;
import kiv.zcu.knowledgeipr.core.query.queries.CountByFieldQuery;
import kiv.zcu.knowledgeipr.core.query.queries.PatentOwnershipEvolutionQuery;
import kiv.zcu.knowledgeipr.core.report.ReportController;
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
        ChartResponse response = reportController.chartQuery(
                new ActivePersonQuery(reportController.getStatsQuery(), ResponseField.AUTHORS.value),
                "activeAuthors.json", DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(
                new ActivePersonQuery(reportController.getStatsQuery(), ResponseField.OWNERS.value),
                "activeOwners.json", DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ObjectSerializationException {
        ChartResponse response = reportController.chartQuery(
                new ActivePersonQuery(reportController.getStatsQuery(), ResponseField.AUTHORS.value),
                "activeAuthors.json", DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ObjectSerializationException {
//        ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_FOS);

        ChartResponse response = reportController.chartQuery(
                new CountByArrayFieldQuery(reportController.getStatsQuery(), ResponseField.FOS, DataSourceType.PUBLICATION),
                "topFos.json", DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ObjectSerializationException {
        //ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_PUBLISHER);

        ChartResponse response = reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.PUBLISHER, DataSourceType.PUBLICATION),
                "prolificPublishers.json", DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ObjectSerializationException {
        //ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_VENUES);

        ChartResponse response = reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.VENUE, DataSourceType.PUBLICATION),
                "prolificVenues.json", DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ObjectSerializationException {
        // ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_KEYWORD);

        ChartResponse response = reportController.chartQuery(
                new CountByArrayFieldQuery(reportController.getStatsQuery(), ResponseField.KEYWORDS, DataSourceType.PUBLICATION),
                "topKeywords.json", DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ObjectSerializationException {
        // ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR);

        ChartResponse response = reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.YEAR, DataSourceType.PUBLICATION),
                "countByYear.json", DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/countsByLang")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByLangPublication() throws ObjectSerializationException {
        // ChartResponse response = reportController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR);

        ChartResponse response = reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.LANG, DataSourceType.PUBLICATION),
                "countByLang.json", DataSourceType.PUBLICATION);

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
        // Active author patents
        reportController.chartQuery(
                new ActivePersonQuery(reportController.getStatsQuery(), ResponseField.AUTHORS.value),
                "activeAuthors.json", DataSourceType.PATENT, overwrite);

        // Active owners patents
        reportController.chartQuery(
                new ActivePersonQuery(reportController.getStatsQuery(), ResponseField.OWNERS.value),
                "activeOwners.json", DataSourceType.PATENT, overwrite);

        // Active authors publication
        reportController.chartQuery(
                new ActivePersonQuery(reportController.getStatsQuery(), ResponseField.AUTHORS.value),
                "activeAuthors.json", DataSourceType.PUBLICATION, overwrite);

        // Counts by fos
        reportController.chartQuery(
                new CountByArrayFieldQuery(reportController.getStatsQuery(), ResponseField.FOS, DataSourceType.PUBLICATION),
                "topFos.json", DataSourceType.PUBLICATION, overwrite);

        // prolific publishers
        reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.PUBLISHER, DataSourceType.PUBLICATION),
                "prolificPublishers.json", DataSourceType.PUBLICATION, overwrite);

        // prolific venues
        reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.VENUE, DataSourceType.PUBLICATION),
                "prolificVenues.json", DataSourceType.PUBLICATION, overwrite);

        // Count by keyword
        reportController.chartQuery(
                new CountByArrayFieldQuery(reportController.getStatsQuery(), ResponseField.KEYWORDS, DataSourceType.PUBLICATION),
                "topKeywords.json", DataSourceType.PUBLICATION, overwrite);

        // Count by year publication
        reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.YEAR, DataSourceType.PUBLICATION),
                "countByYear.json", DataSourceType.PUBLICATION, overwrite);

        // Count by lang
        reportController.chartQuery(
                new CountByFieldQuery(reportController.getStatsQuery(), ResponseField.LANG, DataSourceType.PUBLICATION),
                "countByLang.json", DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().build();
    }
}
