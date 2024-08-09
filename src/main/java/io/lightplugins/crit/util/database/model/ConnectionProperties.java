package io.lightplugins.crit.util.database.model;

import io.lightplugins.crit.util.manager.FileManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConnectionProperties {

    private final long idleTimeout, maxLifetime, connectionTimeout, leakDetectionThreshold, keepAliveTime;
    private final int minimumIdle, maximumPoolSize;
    private final String testQuery, characterEncoding;

    public static ConnectionProperties fromConfig(FileManager config) {

        String rootPath = "storage.advanced.";

        long connectionTimeout = config.getInt(rootPath + "connection-timeout");
        long idleTimeout = config.getInt(rootPath + "idle-timeout");
        long keepAliveTime = config.getInt(rootPath + "keep-alive-time");
        long maxLifeTime = config.getInt(rootPath + "max-life-time");
        int minimumIdle = config.getInt(rootPath + "minimum-idle");
        int maximumPoolSize = config.getInt(rootPath + "maximum-pool-size");
        long leakDetectionThreshold = config.getInt(rootPath + "leak-detection-threshold");
        String characterEncoding = config.getString(rootPath + "character-encoding");
        String testQuery = config.getString(rootPath + "connection-test-query");
        return new ConnectionProperties(idleTimeout, maxLifeTime, connectionTimeout, leakDetectionThreshold, keepAliveTime, minimumIdle, maximumPoolSize, testQuery,characterEncoding);
    }
}
