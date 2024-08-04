package io.lightplugins.crit.modules.roles.language;

public enum Translation {

    /*
        Translations for the roles module
     */

    NO_PERMISSION(":no_entry:  Du hast für diesen Befehl keine Berechtigung.")
    ;

    private final String path;
    Translation(String path) { this.path = path; }
    public String getMessage() {
        return path;
    }
}
