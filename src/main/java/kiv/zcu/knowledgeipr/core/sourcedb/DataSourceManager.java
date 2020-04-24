package kiv.zcu.knowledgeipr.core.sourcedb;

import kiv.zcu.knowledgeipr.core.model.search.SearchEngineName;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSource;
import kiv.zcu.knowledgeipr.core.sourcedb.datasearch.DataSourceType;
import kiv.zcu.knowledgeipr.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class DataSourceManager {

    // TODO: If necessary, change to return List of DataSource instead of String

    /**
     * Returns a list of data sources as strings which are associated with the specified
     * dataSourceType parameter.
     *
     * @param dataSourceType - Source type of data for which to return a list of concrete data sources
     * @return List of data sources as strings. If incorrect dataSourceType parameter is passed, empty
     * list is returned.
     */
    public static List<String> getDataSourcesForSourceTypeAsString(DataSourceType dataSourceType, SearchEngineName searchEngineName) {
        switch (searchEngineName) {
            case elastic:
                return getDataSourcesForSourceTypeAsStringElastic(dataSourceType);
            case mongo:
                getDataSourcesForSourceTypeAsStringMongo(dataSourceType);
                break;
        }

        return new ArrayList<>();
    }

    public static List<DataSource> getDataSourcesForSourceType(DataSourceType dataSourceType, SearchEngineName searchEngineName) {
        switch (searchEngineName) {
            case elastic:
                return getDataSourcesForSourceTypeElastic(dataSourceType);
            case mongo:
                getDataSourcesForSourceTypeAsStringMongo(dataSourceType);
                break;
        }

        return new ArrayList<>();
    }

    private static List<String> getDataSourcesForSourceTypeAsStringElastic(DataSourceType dataSourceType) {
        String prefix = AppConstants.ELASTIC_INDEX_PREFIX;

        List<String> dataSources = new ArrayList<>();
        switch (dataSourceType) {
            case PUBLICATION:
                dataSources.add(prefix + DataSource.MAG.value);
                dataSources.add(prefix + DataSource.SPRINGER.value);
                break;
            case PATENT:
                dataSources.add(prefix + DataSource.USPTO.value);
                dataSources.add(prefix + DataSource.PATSTAT.value);
                break;
            case ALL:
                dataSources.add(prefix + DataSource.MAG.value);
                dataSources.add(prefix + DataSource.SPRINGER.value);
                dataSources.add(prefix + DataSource.USPTO.value);
                dataSources.add(prefix + DataSource.PATSTAT.value);
                break;
        }

        return dataSources;
    }

    private static List<String> getDataSourcesForSourceTypeAsStringMongo(DataSourceType dataSourceType) {
        List<String> dataSources = new ArrayList<>();
        switch (dataSourceType) {
            case PUBLICATION:
                dataSources.add(DataSourceType.PUBLICATION.value);
                break;
            case PATENT:
                dataSources.add(DataSourceType.PATENT.value);
                break;
            case ALL:
                dataSources.add(DataSourceType.PUBLICATION.value);
                dataSources.add(DataSourceType.PATENT.value);
                break;
        }

        return dataSources;
    }

    private static List<DataSource> getDataSourcesForSourceTypeElastic(DataSourceType dataSourceType) {
        List<DataSource> dataSources = new ArrayList<>();
        switch (dataSourceType) {
            case PUBLICATION:
                dataSources.add(DataSource.MAG);
                dataSources.add(DataSource.SPRINGER);
                break;
            case PATENT:
                dataSources.add(DataSource.USPTO);
                break;
            case SPRINGER:
                dataSources.add(DataSource.SPRINGER);
                break;
            case ALL:
                dataSources.add(DataSource.MAG);
                dataSources.add(DataSource.SPRINGER);
                dataSources.add(DataSource.USPTO);
                dataSources.add(DataSource.PATSTAT);
                break;
        }

        return dataSources;
    }

    /**
     * Returns a data category (patents or publications) for the specified source of data represented
     * as string
     *
     * @param dataSource - The source of data as string
     * @return Data category
     */
    public static DataSourceType getTypeForDataSource(String dataSource) {
        //TODO: change
        if (dataSource.equals("mag") || dataSource.equals("springer")) {
            return DataSourceType.PUBLICATION;
        } else {
            return DataSourceType.PATENT;
        }
    }
}