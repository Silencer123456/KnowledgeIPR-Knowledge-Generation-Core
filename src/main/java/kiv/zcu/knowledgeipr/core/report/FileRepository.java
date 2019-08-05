package kiv.zcu.knowledgeipr.core.report;

import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.utils.AppConstants;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.errorhandling.ObjectSerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

public class FileRepository implements IReportRepository {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @Override
    public boolean save(IReport object, String name) {
        try {
            String json = SerializationUtils.serializeObject(object);

            Properties properties = AppServletContextListener.getProperties();
            String basePath = properties.getProperty(AppConstants.REPORTS_RESOURCE);

            new File(basePath + name).getParentFile().mkdirs();
            LOGGER.info("Saving report to " + basePath + name);

            Files.write(Paths.get(basePath + name), json.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException | ObjectSerializationException e) {
            e.printStackTrace();
            LOGGER.warning("Report " + name + " could not be saved.");
        }

        return false;
    }
}
