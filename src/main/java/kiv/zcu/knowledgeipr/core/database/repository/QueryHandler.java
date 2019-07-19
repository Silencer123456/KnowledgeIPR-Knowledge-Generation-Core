package kiv.zcu.knowledgeipr.core.database.repository;

import kiv.zcu.knowledgeipr.core.database.dbconnection.DbManager;
import kiv.zcu.knowledgeipr.core.database.dto.QueryDto;
import kiv.zcu.knowledgeipr.core.query.Query;

import java.sql.SQLException;
import java.util.logging.Logger;

public class QueryHandler {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private IRepository<QueryDto> repository;

    public QueryHandler() {
        try {
            repository = new QueryRepository(new DbManager());
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.severe("Connection to the database could not be established");
            e.printStackTrace();
        }
    }

    public void checkCache(Query query, int page, int limit) {

    }

    public void addNewQuery(QueryDto query) {
        repository.add(query);

        //TODO: check success... log report.....
    }
}
