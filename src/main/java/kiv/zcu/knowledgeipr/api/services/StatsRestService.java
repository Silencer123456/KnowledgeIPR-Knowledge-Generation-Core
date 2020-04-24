package kiv.zcu.knowledgeipr.api.services;

import kiv.zcu.knowledgeipr.api.errorhandling.ObjectSerializationException;
import kiv.zcu.knowledgeipr.api.filter.Logged;
import kiv.zcu.knowledgeipr.api.response.BaseResponse;
import kiv.zcu.knowledgeipr.api.response.IResponse;
import kiv.zcu.knowledgeipr.api.response.ResponseStatus;
import kiv.zcu.knowledgeipr.core.controller.DataAccessController;
import kiv.zcu.knowledgeipr.core.model.report.ReportFilename;
import kiv.zcu.knowledgeipr.core.model.search.SearchEngineName;
import kiv.zcu.knowledgeipr.core.model.search.aggqueries.*;
import kiv.zcu.knowledgeipr.core.sourcedb.DataSourceManager;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSource;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.ResponseField;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.elastic.ElasticQueryRunner;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.interfaces.IQueryRunner;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.mongo.MongoQueryRunner;
import kiv.zcu.knowledgeipr.utils.SerializationUtils;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

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
                new ActivePersonAggregation(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/activeOwnersPatents")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveOwnersPatents() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new ActivePersonAggregation(mongoQueryRunner, ResponseField.OWNERS.value),
                ReportFilename.ACTIVE_OWNERS.value, DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Path("/activeAuthorsPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getActiveAuthorsPublications() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new ActivePersonAggregation(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByFos")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByFosPublications() throws ObjectSerializationException {
        List<DataSource> indexes = new ArrayList<>();
        indexes.add(DataSource.MAG);

        IResponse response = dataAccessController.chartQuery(
                new CountByStringArrayFieldAggregation(elasticQueryRunner, ResponseField.FOS, indexes),
                ReportFilename.TOP_FOS.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/prolificPublishers")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificPublishers() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.PUBLISHER, DataSource.MAG),
                ReportFilename.COUNT_BY_PUBLISHER.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/prolificVenues")
    @Produces("application/json")
    public javax.ws.rs.core.Response getProlificVenues() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.VENUE, DataSource.MAG),
                ReportFilename.COUNT_BY_VENUES.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByKeywords")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountByKeywords() throws ObjectSerializationException {
        List<DataSource> indexes = new ArrayList<>();
        indexes.add(DataSource.MAG);

        IResponse response = dataAccessController.chartQuery(
                new CountByStringArrayFieldAggregation(elasticQueryRunner, ResponseField.KEYWORDS, indexes),
                ReportFilename.COUNT_BY_KEYWORD.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/dateHistogram")
    @Produces("application/json")
    public javax.ws.rs.core.Response getDateHistogram(@QueryParam("dataSource") String dataSource) throws ObjectSerializationException {
        DataSourceType sourceType = DataSourceType.getByValue(dataSource);
        if (sourceType == null) {
            sourceType = DataSourceType.ALL;
        }

        List<DataSource> indexes = DataSourceManager.getDataSourcesForSourceType(sourceType, SearchEngineName.elastic);

        IResponse response;
        if (sourceType == DataSourceType.PUBLICATION) { // MAG data do not contain dates, only years, so do only count aggregation
            response = dataAccessController.chartQuery(
                    new CountByStringArrayFieldAggregation(elasticQueryRunner, ResponseField.YEAR, indexes),
                    ReportFilename.COUNT_BY_YEAR.value, sourceType);
        } else {
            indexes.remove(DataSource.MAG);
            response = dataAccessController.chartQuery(
                    new DateHistogramAggregation(elasticQueryRunner, indexes),
                    ReportFilename.DATE_HISTOGRAM.value,
                    sourceType
            );
        }

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByYearPublications")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByYearPublications() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.YEAR, DataSource.MAG),
                ReportFilename.COUNT_BY_YEAR.value, DataSourceType.PUBLICATION);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @GET
    @Logged
    @Path("/countsByLang")
    @Produces("application/json")
    public javax.ws.rs.core.Response getCountsByLangPublication() throws ObjectSerializationException {
        IResponse response = dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.LANG, DataSource.MAG),
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
                new PatentOwnershipEvolutionAggregation(elasticQueryRunner, ownersName, category),
                "ownerEvol" + ownersName + category + ".json", DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(response)).build();
    }

    @POST
    @Logged
    @Path("/generateStats/{overwrite}")
    public javax.ws.rs.core.Response generateStats(@PathParam("overwrite") boolean overwrite) throws ObjectSerializationException {
        List<DataSource> indexes = new ArrayList<>();
        indexes.add(DataSource.MAG);

        // Active author patents
        dataAccessController.chartQuery(
                new ActivePersonAggregation(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PATENT, overwrite);

        // Active owners patents
        dataAccessController.chartQuery(
                new ActivePersonAggregation(mongoQueryRunner, ResponseField.OWNERS.value),
                ReportFilename.ACTIVE_OWNERS.value, DataSourceType.PATENT, overwrite);

        // Active authors publication
        dataAccessController.chartQuery(
                new ActivePersonAggregation(mongoQueryRunner, ResponseField.AUTHORS.value),
                ReportFilename.ACTIVE_AUTHORS.value, DataSourceType.PUBLICATION, overwrite);

        // Counts by fos
        dataAccessController.chartQuery(
                new CountByStringArrayFieldAggregation(elasticQueryRunner, ResponseField.FOS, indexes),
                ReportFilename.TOP_FOS.value, DataSourceType.PUBLICATION, overwrite);

        // prolific publishers
        dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.PUBLISHER, indexes),
                ReportFilename.COUNT_BY_PUBLISHER.value, DataSourceType.PUBLICATION, overwrite);

        // prolific venues
        dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.VENUE, indexes),
                ReportFilename.COUNT_BY_VENUES.value, DataSourceType.PUBLICATION, overwrite);

        // Count by keyword
        dataAccessController.chartQuery(
                new CountByStringArrayFieldAggregation(elasticQueryRunner, ResponseField.KEYWORDS, indexes),
                ReportFilename.COUNT_BY_KEYWORD.value, DataSourceType.PUBLICATION, overwrite);

        // Count by year publication
        dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.YEAR, indexes),
                ReportFilename.COUNT_BY_YEAR.value, DataSourceType.PUBLICATION, overwrite);

        // Count by lang
        dataAccessController.chartQuery(
                new CountByFieldAggregation(elasticQueryRunner, ResponseField.LANG, indexes),
                ReportFilename.COUNT_BY_LANG.value, DataSourceType.PUBLICATION);

        // Date Histogram
        dataAccessController.chartQuery(
                new DateHistogramAggregation(elasticQueryRunner, indexes),
                ReportFilename.DATE_HISTOGRAM.value,
                DataSourceType.PATENT);

        return javax.ws.rs.core.Response.ok().entity(SerializationUtils.serializeObject(new BaseResponse(ResponseStatus.SUCCESS, "OK"))).build();
    }
}
