package io.lightplugins.crit.modules.roles;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.roles.commands.GiveMember;
import io.lightplugins.crit.modules.roles.commands.RemoveMember;
import io.lightplugins.crit.modules.roles.listener.RegisterCommands;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.interfaces.LightModule;
import io.lightplugins.crit.util.manager.FileManager;

public class LightRoles implements LightModule {

    @Override
    public void enable() {

        FileManager config = new FileManager("config.yml");
        String host = config.getString("database.host");
        LightPrinter.print("Connecting to database at " + host);

        // register commands
        LightMaster.instance.getShardManager().addEventListener(
                new GiveMember(),
                new RemoveMember(),
                new RegisterCommands()
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
        return true;
    }

    @Override
    public String getName() {
        return "LightRoles";
    }
}
