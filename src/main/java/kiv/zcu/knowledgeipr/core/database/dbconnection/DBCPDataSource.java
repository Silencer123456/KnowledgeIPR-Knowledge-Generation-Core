package kiv.zcu.knowledgeipr.core.database.dbconnection;


import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.utils.AppConstants;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DBCPDataSource {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static BasicDataSource ds = new BasicDataSource();

    static {
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

            //ds.setDriver(DriverManager.getDriver(driver));
            ds.setDriverClassName(driver);
            ds.setUrl(host);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setMaxOpenPreparedStatements(100);

            LOGGER.info("host: " + host + "\nusername: " + username + "\npassword: " + password + "\ndriver: " + driver);
            LOGGER.info("--------------------------");
            LOGGER.info("DRIVER: " + driver);
            LOGGER.info("CONNECTION: " + ds.getConnection());

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            LOGGER.severe("Could not establish connection to the database.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
