package kiv.zcu.knowledgeipr.model;

import java.sql.Connection;

public class DbResponseDAO implements ResponseDAO {

    private Connection connection;

    public DbResponseDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Response response) {
        
    }
}
