package kiv.zcu.knowledgeipr.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Gets called at the start of the application.
 * Preloads properties files and makes them accessible
 */
public class AppServletContextListener implements ServletContextListener {
    private static Properties properties = new Properties();

    public static Properties getProperties() {
        return properties;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String cfgfile = servletContextEvent.getServletContext().getInitParameter("config_file");
        try {
            properties.load(new FileInputStream(cfgfile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
