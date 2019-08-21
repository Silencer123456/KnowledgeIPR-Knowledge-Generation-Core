package kiv.zcu.knowledgeipr.core.database.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// !!!NOT IN USE!!!
public class BasicConnectionPool implements ConnectionPool {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static int INITIAL_POOL_SIZE = 10;
    private static int MAX_POOL_SIZE = 30;
    private String url;
    private String user;
    private String password;
    private List<Connection> pool;
    private List<Connection> usedConnections = new ArrayList<>();

    public BasicConnectionPool(String url, String user, String password, List<Connection> pool) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.pool = pool;
    }

    public static BasicConnectionPool create(String url, String user, String password) throws SQLException {
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection(url, user, password));
        }
        return new BasicConnectionPool(url, user, password, pool);
    }

    private static Connection createConnection(
            String url, String user, String password)
            throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (pool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                pool.add(createConnection(url, user, password));
            } else {
                throw new RuntimeException(
                        "Maximum pool size reached, no available connections!");
            }
        }

        Connection connection = pool
                .remove(pool.size() - 1);
        usedConnections.add(connection);

        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        LOGGER.info("RELEASE connection");
        pool.add(connection);
        return usedConnections.remove(connection);
    }

    @Override
    public void commitAndRelease(Connection connection) {
        LOGGER.info("INIT COMMIT");
        try {
            connection.commit();
            releaseConnection(connection);
        } catch (SQLException e) {
            LOGGER.warning("COMMIT failed with error: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("COMMIT SUCCESS");
    }

    @Override
    public void rollbackAndRelease(Connection connection) {
        LOGGER.info("INIT ROLLBACK");
        try {
            // transaction rollback
            connection.rollback();
            releaseConnection(connection);
        } catch (SQLException e) {
            LOGGER.warning("ROLLBACK failed with error: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("ROLLBACK SUCCESS");
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
