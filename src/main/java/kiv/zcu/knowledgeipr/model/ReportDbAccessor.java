package kiv.zcu.knowledgeipr.model;

import kiv.zcu.knowledgeipr.core.Query;
import kiv.zcu.knowledgeipr.core.Report;
import kiv.zcu.knowledgeipr.core.dbconnection.DbManager;

import java.sql.Connection;
import java.sql.SQLException;

public class ReportDbAccessor {

    private Connection connection;

    public ReportDbAccessor() {

        DbManager dbManager = new DbManager();
        try {
            connection = dbManager.createConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addReportWithQuery(Query query, Report report) {

    }
}
