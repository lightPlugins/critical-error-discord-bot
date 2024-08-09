package io.lightplugins.crit.util.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.PooledDatabase;
import io.lightplugins.crit.util.database.model.ConnectionProperties;
import io.lightplugins.crit.util.database.model.DatabaseTypes;

import java.io.File;
import java.io.IOException;

public class SQLiteDatabase extends PooledDatabase {


    private static final String FILE_NAME = "database.db";
    private final ConnectionProperties connectionProperties;
    private final File dataFolder;

    public SQLiteDatabase(LightMaster plugin, ConnectionProperties connectionProperties) {
        super(plugin);
        this.connectionProperties = connectionProperties;
        this.dataFolder = new File(System.getProperty("user.dir"));
    }

    @Override
    public DatabaseTypes getDatabaseType() {
        return DatabaseTypes.SQLITE;
    }

    @Override
    public void connect() {

        File databaseFile = createDBFile();
        final HikariConfig hikari = new HikariConfig();

        hikari.setPoolName("light-" + POOL_COUNTER.getAndIncrement());

        hikari.setDriverClassName("org.sqlite.JDBC");
        hikari.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());

        hikari.setConnectionTimeout(connectionProperties.getConnectionTimeout());
        hikari.setIdleTimeout(connectionProperties.getIdleTimeout());
        hikari.setKeepaliveTime(connectionProperties.getKeepAliveTime());
        hikari.setMaxLifetime(connectionProperties.getMaxLifetime());
        hikari.setMinimumIdle(connectionProperties.getMinimumIdle());
        hikari.setMaximumPoolSize(10);
        hikari.setLeakDetectionThreshold(connectionProperties.getLeakDetectionThreshold());
        hikari.setConnectionTestQuery(connectionProperties.getTestQuery());

        this.hikari = new HikariDataSource(hikari);
    }

    private File createDBFile() {
        File databaseFile = new File(dataFolder, FILE_NAME);
        try {
            if(!databaseFile.createNewFile()) {
                LightPrinter.printError("Unable to create " + FILE_NAME + ". File already exists.");
            }
        } catch (IOException e) {
            LightPrinter.printError("Unable to create " + FILE_NAME);
            throw new RuntimeException("Unable to create " + FILE_NAME, e);
        }
        return databaseFile;
    }
}
