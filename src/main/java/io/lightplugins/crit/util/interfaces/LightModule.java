package io.lightplugins.crit.util.interfaces;

public interface LightModule {

    void enable();

    void disable();

    void reload();

    boolean enabled();

    String getName();

}
