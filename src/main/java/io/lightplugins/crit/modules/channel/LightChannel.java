package io.lightplugins.crit.modules.channel;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.channel.listener.AutoChannel;
import io.lightplugins.crit.util.interfaces.LightModule;
import io.lightplugins.crit.util.manager.FileManager;
import lombok.Getter;

@Getter
public class LightChannel implements LightModule {

    public static LightChannel instance;
    private String autoChannelName;
    private FileManager autoChannelConfig;

    @Override
    public void enable() {
        instance = this;

        autoChannelConfig = new FileManager("autochannel.yml");
        autoChannelName = autoChannelConfig.getString("autochannel.channel-name");

        if(autoChannelName == null) {
            autoChannelName = "NOT_FOUND";
        }

        LightMaster.instance.getShardManager().addEventListener(
                new AutoChannel()
        );

    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public String getName() {
        return "LightChannel";
    }
}
