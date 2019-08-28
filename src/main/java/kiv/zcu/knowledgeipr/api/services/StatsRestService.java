package kiv.zcu.knowledgeipr.api.services;

import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.api.response.ChartResponse;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.report.ReportFilename;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.ActivePersonQuery;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.CountByArrayFieldQuery;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.CountByFieldQuery;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.PatentOwnershipEvolutionQuery;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import javax.ws.rs.*;

@Path("/stats/")
public class StatsRestService {

    private DataAccessController dataAccessController;

    public StatsRestService(DataAccessController dataAccessController) {
        this.dataAccessController = dataAccessController;
    }

    @GET
    @Logged
    @Path("/activeAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPatents() throws ObjectSerializationException {
        ChartResponse response = dataAccessController.chartQuery(
                new ActivePersonQuery(dataAccessController.getQueryRunner(), ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ObjectSerializationException {
        ChartResponse response = dataAccessController.chartQuery(
                new ActivePersonQuery(dataAccessController.getQueryRunner(), ResponseField.OWNERS.value),
                ReportFilename.ACTIVE_OWNERS.value, DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ObjectSerializationException {
        ChartResponse response = dataAccessController.chartQuery(
                new ActivePersonQuery(dataAccessController.getQueryRunner(), ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ObjectSerializationException {
//        ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_FOS);

        ChartResponse response = dataAccessController.chartQuery(
                new CountByArrayFieldQuery(dataAccessController.getQueryRunner(), ResponseField.FOS, DataSourceType.PUBLICATION),
                ReportFilename.TOP_FOS.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ObjectSerializationException {
        //ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_PUBLISHER);

        ChartResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.PUBLISHER, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_PUBLISHER.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ObjectSerializationException {
        //ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_VENUES);

        ChartResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.VENUE, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_VENUES.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ObjectSerializationException {
        // ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_KEYWORD);

        ChartResponse response = dataAccessController.chartQuery(
                new CountByArrayFieldQuery(dataAccessController.getQueryRunner(), ResponseField.KEYWORDS, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_KEYWORD.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ObjectSerializationException {
        // ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR);

        ChartResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.YEAR, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_YEAR.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByLang")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByLangPublication() throws ObjectSerializationException {
        // ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR);

        ChartResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.LANG, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_LANG.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/patentOwnershipEvolution/{owner}/{category}")
    @Produces("application/json")
    public javax.ws.rs.core.Response getPatentOwnershipEvolution(@PathParam("owner") String ownersName,
                                                                 @PathParam("category") String category)
            throws ObjectSerializationException {

        // TODO! check category valid
        // TODO! better way of disambiguaiting reports filenames, probably saving to database instead

        ChartResponse response = dataAccessController.chartQuery(
                new PatentOwnershipEvolutionQuery(dataAccessController.getQueryRunner(), ownersName, category),
                "ownerEvol" + ownersName + category + ".json", DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Logged
    @Path("/generateStats/{overwrite}")
    public javax.ws.rs.core.Response generateStats(@PathParam("overwrite") boolean overwrite) throws ObjectSerializationException {
        // Active author patents
        dataAccessController.chartQuery(
                new ActivePersonQuery(dataAccessController.getQueryRunner(), ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PATENT, overwrite);

        // Active owners patents
        dataAccessController.chartQuery(
                new ActivePersonQuery(dataAccessController.getQueryRunner(), ResponseField.OWNERS.value),
                ReportFilename.ACTIVE_OWNERS.value, DataSourceType.PATENT, overwrite);

        // Active authors publication
        dataAccessController.chartQuery(
                new ActivePersonQuery(dataAccessController.getQueryRunner(), ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PUBLICATION, overwrite);

        // Counts by fos
        dataAccessController.chartQuery(
                new CountByArrayFieldQuery(dataAccessController.getQueryRunner(), ResponseField.FOS, DataSourceType.PUBLICATION),
                ReportFilename.TOP_FOS.value, DataSourceType.PUBLICATION, overwrite);

        // prolific publishers
        dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.PUBLISHER, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_PUBLISHER.value, DataSourceType.PUBLICATION, overwrite);

        // prolific venues
        dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.VENUE, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_VENUES.value, DataSourceType.PUBLICATION, overwrite);

        // Count by keyword
        dataAccessController.chartQuery(
                new CountByArrayFieldQuery(dataAccessController.getQueryRunner(), ResponseField.KEYWORDS, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_KEYWORD.value, DataSourceType.PUBLICATION, overwrite);

        // Count by year publication
        dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.YEAR, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_YEAR.value, DataSourceType.PUBLICATION, overwrite);

        // Count by lang
        dataAccessController.chartQuery(
                new CountByFieldQuery(dataAccessController.getQueryRunner(), ResponseField.LANG, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_LANG.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().build();
    }
}
