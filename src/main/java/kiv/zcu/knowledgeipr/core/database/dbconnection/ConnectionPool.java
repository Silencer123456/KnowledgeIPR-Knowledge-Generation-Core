package kiv.zcu.knowledgeipr.core.database.dbconnection;

import java.sql.Connection;
import java.sql.SQLException;

// !!!NOT IN USE!!!
public interface ConnectionPool {
    Connection getConnection() throws SQLException;

    boolean releaseConnection(Connection connection);

    void commitAndRelease(Connection connection);

    void rollbackAndRelease(Connection connection);

    String getUrl();

    String getUser();

    String getPassword();
}
