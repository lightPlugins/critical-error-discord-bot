package io.lightplugins.crit.modules.watchdog;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.watchdog.logging.ChangeName;
import io.lightplugins.crit.modules.watchdog.logging.JoinGuild;
import io.lightplugins.crit.modules.watchdog.logging.VoiceKick;
import io.lightplugins.crit.util.interfaces.LightModule;
import io.lightplugins.crit.util.manager.FileManager;
import lombok.Getter;

@Getter
public class LightWatchdog implements LightModule {

    public static LightWatchdog instance;
    private FileManager watchdogConfig;

    @Override
    public void enable() {

        instance = this;
        watchdogConfig = new FileManager("watchdog.yml");

        LightMaster.instance.getShardManager().addEventListener(
                new JoinGuild(),
                new VoiceKick(),
                new ChangeName()
        );

    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        watchdogConfig.reloadConfig();
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public String getName() {
        return "LightWatchdog";
    }
}
