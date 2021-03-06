package kiv.zcu.knowledgeipr.core.knowledgedb.dbconnection;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.utils.AppConstants;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * - Enables access to the pool of connections to the SQL database.
 * - Uses thread scoping for connections.
 * - Supports transaction operations and rollbacks.
 * In order for the connection to be closed properly,
 * the startTransaction() method has to be called before performing any actions on the database
 * and commitAndClose() or rollback() have to be called after the actions were performed
 */
public class DataSourceUtils {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // c3p0 connection pool
    private static ComboPooledDataSource ds = new ComboPooledDataSource();
    // The current database connection object associated with the thread
    private static ThreadLocal<Connection> tl = new ThreadLocal<>();

    static {
        try {
            setUp();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the connection from the thread
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        // Get conneciton from the thread
        Connection conn = tl.get();
        if (conn == null) {
            LOGGER.finer("NEW CONNECTION");
            conn = ds.getConnection();
            // bind to the current thread
            tl.set(conn);
        }
        return conn;
    }

    private static Connection setUp() throws SQLException {
        Properties properties = AppServletContextListener.getProperties();
        String basePath = properties.getProperty(AppConstants.DB_CONFIG_RESOURCE);

        Properties prop = new Properties();
        String host;
        String username;
        String password;
        String driver;
        try {
            prop.load(new FileInputStream(basePath));
            host = prop.getProperty("host");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
            driver = prop.getProperty("driver");

            ds.setDriverClass(driver);
            ds.setJdbcUrl(host);
            ds.setUser(username);
            ds.setPassword(password);

            LOGGER.info("host: " + host + "\nusername: " + username + "\npassword: " + password + "\ndriver: " + driver);
            LOGGER.info("--------------------------");
            LOGGER.info("DRIVER: " + driver);
            LOGGER.info("CONNECTION: " + ds.getConnection());

        } catch (PropertyVetoException | IOException e) {
            e.printStackTrace();
            LOGGER.severe("Could not establish connection to the database.");
        }

        return ds.getConnection();
    }

    /**
     * Takes the data source
     *
     * @return
     */
    public static DataSource getDataSource() {
        return ds;
    }

    /**
     * Releases resources
     *
     * @param st
     * @param rs
     */
    public static void closeResource(Statement st, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(st);
    }

    /**
     * Releases resources
     *
     * @param conn
     * @param st
     * @param rs
     */
    public static void closeResource(Connection conn, Statement st, ResultSet rs) {
        closeResource(st, rs);
        closeConn(conn);
    }

    /**
     * release connection
     *
     * @param conn
     */
    public static void closeConn(Connection conn) {
        if (conn != null) {
            try {
                close(conn);
                // untied with the thread
                tl.remove();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
    }

    /**
     * releases statement
     *
     * @param st
     */
    public static void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            st = null;
        }
    }

    /**
     * Releases the result set
     *
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs = null;
        }
    }

    /**
     * Opens the transaction
     *
     * @throws SQLException
     */
    public static void startTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public static void close() {
        Connection conn;
        try {
            conn = getConnection();
            // close the resource
            close(conn);
            // Unbind
            tl.remove();
        } catch (SQLException e) {
            LOGGER.warning("CLOSE failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void close(Connection connection) throws SQLException {
        connection.close();
        LOGGER.finer("CONNECTION CLOSED");
    }

    /**
     * Transaction is committed and the connection is released
     */
    public static void commitAndClose() {
        LOGGER.finer("-------------");
        LOGGER.finer("INIT COMMIT");
        Connection conn;
        try {
            conn = getConnection();
            // transaction commit
            conn.commit();
            // close the resource
            close(conn);
            // Unbind
            tl.remove();
            LOGGER.finer("COMMIT SUCCESS");
            LOGGER.finer("CONNECTION CLOSED");
        } catch (SQLException e) {
            LOGGER.warning("COMMIT FAIL with error: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.finer("-------------");
    }

    /**
     * Transaction rollback and release resources
     */
    public static void rollbackAndClose() {
        LOGGER.info("-------------");
        LOGGER.info("INIT ROLLBACK");

        Connection conn;
        try {
            conn = getConnection();
            // transaction rollback
            conn.rollback();
            // close the resource
            close(conn);
            // Release the version
            tl.remove();
            LOGGER.info("ROLLBACK SUCCESS");

        } catch (SQLException e) {
            LOGGER.warning("ROLLBACK FAIL with error: " + e.getMessage());
            e.printStackTrace();
        }

        LOGGER.info("-------------");
    }
}
