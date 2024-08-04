package io.lightplugins.crit.enums;

public enum TextChannels {

    GIVEAWAY_CHANNEL(1076991560463417494L),
    LEVEL_CHANNEL(1076991751505588334L),
    POLL(1076991560463417494L);

    private final long id;

    TextChannels(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
