package io.lightplugins.crit.util.database.model;

public enum TableNames {
        /*
        Translations for the roles module
     */

    USER_DATA("userData")
    ;

    private final String path;
    TableNames(String path) { this.path = path; }
    public String getTableName() {
        return path;
    }

}
