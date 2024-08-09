package io.lightplugins.crit.util.database.model;

import io.lightplugins.crit.util.manager.FileManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

@Getter
@AllArgsConstructor
public class DatabaseCredentials {

    private final String host, databaseName, userName, password;
    private final int port;

    public static DatabaseCredentials fromConfig(FileManager config) {

        String rootPath = "storage.";

        String host = config.getString(rootPath + "host");
        String dbName = config.getString(rootPath + "database");
        String userName = config.getString(rootPath + "username");
        String password = config.getString(rootPath + "password");
        int port = config.getInt(rootPath + "port");

        Validate.notNull(host);
        Validate.notNull(dbName);
        Validate.notNull(userName);
        Validate.notNull(password);

        return new DatabaseCredentials(host, dbName, userName, password, port);
    }

}
