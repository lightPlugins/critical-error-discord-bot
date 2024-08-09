package io.lightplugins.crit.util.database;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.model.DatabaseTypes;

import java.sql.*;
import java.sql.Connection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class SQLDatabase {

    protected final LightMaster plugin;
    private static final ExecutorService dbExecutor = Executors.newFixedThreadPool(10);


    SQLDatabase(LightMaster plugin) {
        this.plugin = plugin;
    }

    public abstract DatabaseTypes getDatabaseType();
    public abstract void connect();
    public abstract void close();
    public abstract Connection getConnection();

    public CompletableFuture<Boolean> insertIntoDatabaseAsync(String sql, Object... replacements) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = getConnection(); PreparedStatement statement = prepareStatement(c, sql, replacements)) {
                int affectedRows = statement.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Could not execute SQL statement", e);
            }
        }, dbExecutor);
    }

    public boolean insertIntoDatabase(String sql, Object... replacements) {
        try (Connection c = getConnection(); PreparedStatement statement = prepareStatement(c, sql, replacements)) {
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not execute SQL statement", e);
        }
    }

    public <T> CompletableFuture<T> queryDatabaseAsync(String sql, Class<T> type, Object... replacements) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection c = getConnection();
                 PreparedStatement statement = prepareStatement(c, sql, replacements);
                 ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return type.cast(rs.getObject(1)); // Assuming the value is in the first column
                } else {
                    throw new RuntimeException("No result found for the query");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Could not execute SQL statement", e);
            }
        }, dbExecutor);
    }

    private PreparedStatement prepareStatement(Connection connection, String sql, Object... replacements) throws SQLException {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);
            this.replaceQueryParameters(statement,replacements);
            return statement;
        } catch (SQLException e) {
            LightPrinter.printError("Could not prepare SQL statement: " + sql);
            e.printStackTrace();
            throw new SQLException("Could not prepare SQL statement", e);
        }
    }

    public void createTable(String tableName, String tableDefinition) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + tableDefinition + ")";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
            LightPrinter.print("[DATABASE] Table " + tableName + " created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating table " + tableName, e);
        }
    }

    public CompletableFuture<Integer> executeSqlFuture(String sql, Object... replacements) {

        CompletableFuture<Integer> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try (Connection c = getConnection(); PreparedStatement statement = prepareStatement(c, sql, replacements)) {
                int affectedLines = statement.executeUpdate();
                future.complete(affectedLines);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not execute SQL statement", e);
            }
        });
        return future;
    }

    private void replaceQueryParameters(PreparedStatement statement, Object[] replacements) {
        if (replacements != null) {
            for (int i = 0; i < replacements.length; i++) {
                int position = i + 1;
                Object value = replacements[i];
                try {
                    statement.setObject(position, value);
                } catch (SQLException e) {
                    LightPrinter.printError("Unable to set query parameter at position " + position + " to " + value + " for query: " + statement);
                    e.printStackTrace();
                    throw new RuntimeException("Failed to set query parameter", e);
                }
            }
        }
    }
}