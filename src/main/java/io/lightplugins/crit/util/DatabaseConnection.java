package io.lightplugins.crit.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private final File dataFolder;
    private HikariDataSource dataSource;

    public DatabaseConnection() {
        this.dataFolder = new File(System.getProperty("user.dir"));
    }

    public void connectToDatabaseViaSQLite() {
        String database = "database.db";
        File databaseFile = new File(dataFolder, database);

        if (!databaseFile.exists()) {
            try {
                if (!databaseFile.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                return;
            }
        }

        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
        } catch (ClassNotFoundException e) {
            // Handle the error
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("SQLite");
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}