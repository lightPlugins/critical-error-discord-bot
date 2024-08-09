package io.lightplugins.crit.util.database.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.database.PooledDatabase;
import io.lightplugins.crit.util.database.model.ConnectionProperties;
import io.lightplugins.crit.util.database.model.DatabaseCredentials;
import io.lightplugins.crit.util.database.model.DatabaseTypes;

public class MySQLDatabase extends PooledDatabase {

    private final DatabaseCredentials credentials;
    private final ConnectionProperties connectionProperties;

    public MySQLDatabase(LightMaster parent, DatabaseCredentials credentials, ConnectionProperties connectionProperties) {
        super(parent);
        this.connectionProperties = connectionProperties;
        this.credentials = credentials;
    }

    @Override
    public void connect() {
        final HikariConfig hikari = new HikariConfig();

        hikari.setPoolName("light-" + POOL_COUNTER.getAndIncrement());

        this.applyCredentials(hikari, credentials, connectionProperties);
        this.applyConnectionProperties(hikari, connectionProperties);
        this.addDefaultDataSourceProperties(hikari);
        this.hikari = new HikariDataSource(hikari);
    }

    private void applyCredentials(HikariConfig hikari, DatabaseCredentials credentials, ConnectionProperties connectionProperties) {
        hikari.setJdbcUrl("jdbc:mysql://" + credentials.getHost() + ":" + credentials.getPort() + "/" + credentials.getDatabaseName() + "?characterEncoding=" + connectionProperties.getCharacterEncoding());
        hikari.setUsername(credentials.getUserName());
        hikari.setPassword(credentials.getPassword());
    }

    private void applyConnectionProperties(HikariConfig hikari, ConnectionProperties connectionProperties) {
        hikari.setConnectionTimeout(connectionProperties.getConnectionTimeout());
        hikari.setIdleTimeout(connectionProperties.getIdleTimeout());
        hikari.setKeepaliveTime(connectionProperties.getKeepAliveTime());
        hikari.setMaxLifetime(connectionProperties.getMaxLifetime());
        hikari.setMinimumIdle(connectionProperties.getMinimumIdle());
        hikari.setMaximumPoolSize(connectionProperties.getMaximumPoolSize());
        hikari.setLeakDetectionThreshold(connectionProperties.getLeakDetectionThreshold());
        hikari.setConnectionTestQuery(connectionProperties.getTestQuery());
    }

    private void addDefaultDataSourceProperties(HikariConfig hikari) {
        hikari.addDataSourceProperty("cachePrepStmts", true);
        hikari.addDataSourceProperty("prepStmtCacheSize", 250);
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikari.addDataSourceProperty("useServerPrepStmts", true);
        hikari.addDataSourceProperty("useLocalSessionState", true);
        hikari.addDataSourceProperty("rewriteBatchedStatements", true);
        hikari.addDataSourceProperty("cacheResultSetMetadata", true);
        hikari.addDataSourceProperty("cacheServerConfiguration", true);
        hikari.addDataSourceProperty("elideSetAutoCommits", true);
        hikari.addDataSourceProperty("maintainTimeStats", false);
    }

    @Override
    public DatabaseTypes getDatabaseType() {
        return DatabaseTypes.MYSQL;
    }
}
