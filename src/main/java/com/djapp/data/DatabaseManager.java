package com.djapp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.djapp.utils.ConfigLoader;


public class DatabaseManager {
    private Connection connection;
    private String url;
    private String user;
    private String password;

    public DatabaseManager() {
        this.url = ConfigLoader.get("db.url");
        this.user = ConfigLoader.get("db.user");
        this.password = ConfigLoader.get("db.password");
    }


    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to MySQL!");
        }
        return connection;
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("🔌 Disconnected from MySQL.");
        }
    }
}