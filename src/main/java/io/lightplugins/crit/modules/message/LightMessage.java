package io.lightplugins.crit.modules.message;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.message.listener.WelcomeMessage;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.interfaces.LightModule;
import io.lightplugins.crit.util.manager.FileManager;
import lombok.Getter;

@Getter
public class LightMessage implements LightModule {

    public static LightMessage instance;
    private FileManager welcomeConfig;

    @Override
    public void enable() {
        instance = this;

        welcomeConfig = new FileManager("welcome.yml");

        LightMaster.instance.getShardManager().addEventListener(
                new WelcomeMessage()
        );

    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {
        welcomeConfig.reloadConfig();
        LightPrinter.print("[CONFIG] Reloaded welcome.yml");
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public String getName() {
        return "LightMessage";
    }
}
