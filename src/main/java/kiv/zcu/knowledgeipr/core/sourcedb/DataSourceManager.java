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
                break;
            case ALL:
                dataSources.add(prefix + DataSource.MAG.value);
                dataSources.add(prefix + DataSource.SPRINGER.value);
                dataSources.add(prefix + DataSource.USPTO.value);
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
}