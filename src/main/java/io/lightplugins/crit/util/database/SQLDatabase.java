package io.lightplugins.crit.util.database;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.model.DatabaseTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public abstract class SQLDatabase {

    protected final LightMaster plugin;

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
        });
    }

    public <T> CompletableFuture<T> getObjectFromDatabaseAsync(String sql, Class<T> type, Object... replacements) {
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
        });
    }

    private PreparedStatement prepareStatement(Connection connection, String sql, Object... replacements) {
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(sql);
            this.replaceQueryParameters(statement,replacements);
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException("Could not prepare SQL statement", e);
        }
    }

    public CompletableFuture<Integer> executeSqlFuture(String sql, Object... replacements) {

        CompletableFuture<Integer> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try (Connection c = getConnection();
                 PreparedStatement statement = prepareStatement(c, sql, replacements)) {
                int affectedLines = statement.executeUpdate();
                future.complete(affectedLines);
            } catch (SQLException e) {
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
                    throw new RuntimeException("Failed to set query parameter", e);
                }
            }
        }
    }
}