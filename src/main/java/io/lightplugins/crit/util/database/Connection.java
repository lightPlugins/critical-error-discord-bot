package io.lightplugins.crit.util.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.manager.FileManager;

import java.io.File;
import java.io.IOException;

public class Connection {

    private final LightMaster master;
    private final File dataFolder;
    private final FileManager databaseCredentials;

    public Connection(LightMaster master, FileManager databaseCredentials) {
        this.master = master;
        this.dataFolder = new File(System.getProperty("user.dir"));
        this.databaseCredentials = databaseCredentials;
    }

    public void connectMariaDB() {

        String host = databaseCredentials.getString("mysql.host");
        String port = databaseCredentials.getString("mysql.port");
        String database = databaseCredentials.getString("mysql.database");
        String user = databaseCredentials.getString("mysql.user");
        String password = databaseCredentials.getString("mysql.password");
        Boolean ssl = databaseCredentials.getBoolean("mysql.ssl");
        Boolean useServerPrepStmts = databaseCredentials.getBoolean("mysql.advanced.useServerPrepStmts");
        Boolean cachePrepStmts = databaseCredentials.getBoolean("mysql.advanced.cachePrepStmts");
        int prepStmtCacheSize = databaseCredentials.getInt("mysql.advanced.prepStmtCacheSize");
        int prepStmtCacheSqlLimit = databaseCredentials.getInt("mysql.advanced.prepStmtCacheSqlLimit");
        int connectionPoolSize = databaseCredentials.getInt("mysql.advanced.connectionPoolSize");

        HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" +host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.addDataSourceProperty("serverName", host);
        hikariConfig.addDataSourceProperty("port", port);
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("user", user);
        hikariConfig.addDataSourceProperty("password", password);
        hikariConfig.addDataSourceProperty("useSSL", ssl);
        hikariConfig.setMaximumPoolSize(connectionPoolSize);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", useServerPrepStmts);
        hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
        master.ds = new HikariDataSource(hikariConfig);
        LightPrinter.print("Connected to MariaDB");
    }

    /**
     * Connects to the SQLite database.
     */
    public void connectSQLite() {
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
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("SQLite");
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setMaximumPoolSize(10);
        master.ds = new HikariDataSource(config);
    }
}
