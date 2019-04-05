package kiv.zcu.knowledgeipr.model;

import kiv.zcu.knowledgeipr.core.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbReportDAO implements ReportDAO {

    private Connection connection;

    public DbReportDAO(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void insert(Report report) { // http://hmkcode.com/java-mysql/
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hmkcode.persons (id ,name) VALUES (NULL , ?)");
           //preparedStatement.setString(1,  person.getName());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
