package kiv.zcu.knowledgeipr.core.dbaccess;

import kiv.zcu.knowledgeipr.app.AppServletContextListener;
import kiv.zcu.knowledgeipr.core.report.IReport;
import kiv.zcu.knowledgeipr.core.utils.Constants;
import kiv.zcu.knowledgeipr.core.utils.SerializationUtils;
import kiv.zcu.knowledgeipr.rest.exception.ObjectSerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class FileRepository implements IReportRepository {
    @Override
    public boolean save(IReport object, String name) {
        try {
            String json = SerializationUtils.serializeObject(object);

            Properties properties = AppServletContextListener.getProperties();
            String basePath = properties.getProperty(Constants.REPORTS_RESOURCE_NAME);

            new File(basePath + name).getParentFile().mkdirs();

            Files.write(Paths.get(basePath + name), json.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException | ObjectSerializationException e) {
            e.printStackTrace();
        }

        return false;
    }
}
