package io.lightplugins.crit.util.models;

import io.lightplugins.crit.util.DatabaseConnection;
import io.lightplugins.crit.util.LightPrinter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTableCreator {

    private final DatabaseConnection databaseConnection;

    public DatabaseTableCreator(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void createTable(String tableName, String columns) {
        // Sanitize inputs
        tableName = tableName.replaceAll("[^a-zA-Z0-9_]", "");
        columns = columns.replaceAll("[^a-zA-Z0-9_, ]", "");

        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ");";

        try (Connection conn = databaseConnection.getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LightPrinter.print("Table " + tableName + " created successfully");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create table " + tableName, e);
        }
    }
}