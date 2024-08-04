package io.lightplugins.crit.enums;

public enum RoleDataPath {

    MEMBER("1262999713175834626"),
    STREAMER("1174634851517874176"),
    IT("1269399942737166426"),
    NITRO_BOOSTER("1134950564732616786"),
    GUEST("1140689400968253471"),
    FRIEND("1085286861280387072"),
    SUPPORTER("1269467265909260360"),
    ADMIN("1195852555599745174"),
    OWNER("1261832512406294528"),
    LEADER("1081002997196267520")
    ;

    private final String path;
    RoleDataPath(String path) { this.path = path; }
    public String getID() {
        return path;
    }

}
