package kiv.zcu.knowledgeipr.api.services;

import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.api.response.IResponse;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.report.ReportFilename;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.ActivePersonQuery;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.CountByArrayFieldQuery;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.CountByFieldQuery;
import kiv.zcu.knowledgeipr.core.model.search.chartquery.PatentOwnershipEvolutionQuery;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.ElasticQueryRunner;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.MongoQueryRunner;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import javax.ws.rs.*;

@Path("/stats/")
public class StatsRestService {

    private DataAccessController dataAccessController;

    /**
     * Provides methods for running concrete queries on the target database
     */
    private IQueryRunner mongoQueryRunner;
    private IQueryRunner elasticQueryRunner;

    public StatsRestService(DataAccessController dataAccessController) {
        this.dataAccessController = dataAccessController;
        mongoQueryRunner = new MongoQueryRunner();
        elasticQueryRunner = new ElasticQueryRunner();
    }

    @GET
    @Logged
    @Path("/activeAuthorsPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPatents() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new ActivePersonQuery(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new ActivePersonQuery(mongoQueryRunner, ResponseField.OWNERS.value),
                ReportFilename.ACTIVE_OWNERS.value, DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new ActivePersonQuery(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ObjectSerializationException {
//        ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_FOS);

        IResponse response = dataAccessController.chartQuery(
                new CountByArrayFieldQuery(elasticQueryRunner, ResponseField.FOS, DataSourceType.PUBLICATION),
                ReportFilename.TOP_FOS.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ObjectSerializationException {
        //ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_PUBLISHER);

        IResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.PUBLISHER, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_PUBLISHER.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ObjectSerializationException {
        //ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_VENUES);

        IResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.VENUE, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_VENUES.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ObjectSerializationException {
        // ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_KEYWORD);

        IResponse response = dataAccessController.chartQuery(
                new CountByArrayFieldQuery(mongoQueryRunner, ResponseField.KEYWORDS, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_KEYWORD.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ObjectSerializationException {
        // ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR);

        IResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.YEAR, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_YEAR.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByLang")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByLangPublication() throws ObjectSerializationException {
        // ChartResponse response = dataAccessController.chartQuery(DataSourceType.PUBLICATION, ReportFilename.COUNT_BY_YEAR);

        IResponse response = dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.LANG, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_LANG.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/patentOwnershipEvolution")
    @Produces("application/json")
    public javax.ws.rs.core.Response getPatentOwnershipEvolution(@QueryParam("owner") String ownersName,
                                                                 @QueryParam("category") String category)
            throws ObjectSerializationException {

        // TODO! check category valid
        // TODO! better way of disambiguaiting reports filenames, probably saving to database instead

        IResponse response = dataAccessController.chartQuery(
                new PatentOwnershipEvolutionQuery(elasticQueryRunner, ownersName, category),
                "ownerEvol" + ownersName + category + ".json", DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Logged
    @Path("/generateStats/{overwrite}")
    public javax.ws.rs.core.Response generateStats(@PathParam("overwrite") boolean overwrite) throws ObjectSerializationException {
        // Active author patents
        dataAccessController.chartQuery(
                new ActivePersonQuery(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PATENT, overwrite);

        // Active owners patents
        dataAccessController.chartQuery(
                new ActivePersonQuery(mongoQueryRunner, ResponseField.OWNERS.value),
                ReportFilename.ACTIVE_OWNERS.value, DataSourceType.PATENT, overwrite);

        // Active authors publication
        dataAccessController.chartQuery(
                new ActivePersonQuery(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PUBLICATION, overwrite);

        // Counts by fos
        dataAccessController.chartQuery(
                new CountByArrayFieldQuery(mongoQueryRunner, ResponseField.FOS, DataSourceType.PUBLICATION),
                ReportFilename.TOP_FOS.value, DataSourceType.PUBLICATION, overwrite);

        // prolific publishers
        dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.PUBLISHER, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_PUBLISHER.value, DataSourceType.PUBLICATION, overwrite);

        // prolific venues
        dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.VENUE, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_VENUES.value, DataSourceType.PUBLICATION, overwrite);

        // Count by keyword
        dataAccessController.chartQuery(
                new CountByArrayFieldQuery(mongoQueryRunner, ResponseField.KEYWORDS, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_KEYWORD.value, DataSourceType.PUBLICATION, overwrite);

        // Count by year publication
        dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.YEAR, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_YEAR.value, DataSourceType.PUBLICATION, overwrite);

        // Count by lang
        dataAccessController.chartQuery(
                new CountByFieldQuery(mongoQueryRunner, ResponseField.LANG, DataSourceType.PUBLICATION),
                ReportFilename.COUNT_BY_LANG.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().build();
    }
}
