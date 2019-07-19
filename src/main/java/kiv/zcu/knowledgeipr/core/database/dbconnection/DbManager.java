package kiv.zcu.knowledgeipr.core.database.dbconnection;

import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.utils.AppConstants;
import org.apache.commons.dbutils.DbUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Provides a connection to SQL database.
 */
public class DbManager {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final int LoginTimeout = 10;

    private Connection connection;

    public DbManager() throws SQLException, ClassNotFoundException {
        createConnection();
    }

    /**
     * Creates connection to the database
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private Connection createConnection() throws SQLException, ClassNotFoundException {
        Properties properties = AppServletContextListener.getProperties();
        String basePath = properties.getProperty(AppConstants.DB_CONFIG_RESOURCE_NAME);

        //String configPath = "mydb.cfg";
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
        } catch (IOException e) {
            LOGGER.severe("Unable to find file in " + basePath);
            e.printStackTrace();

            host = "Unknown HOST";
            username = "Unknown USER";
            password = "Unknown PASSWORD";
            driver = "Unknown DRIVER";
        }

        LOGGER.info("host: " + host + "\nusername: " + username + "\npassword: " + password + "\ndriver: " + driver);
// TODO: handle case when db could not be loaded
        Class.forName(driver);
        LOGGER.info("--------------------------");
        LOGGER.info("DRIVER: " + driver);
        LOGGER.info("Set Login Timeout: " + LoginTimeout);
        DriverManager.setLoginTimeout(LoginTimeout);
        Connection connection = DriverManager.getConnection(host, username, password);
        LOGGER.info("CONNECTION: " + connection);

        this.connection = connection;
        return connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        DbUtils.closeQuietly(connection);
    }
}
