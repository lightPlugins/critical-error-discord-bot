package io.lightplugins.crit.modules.roles;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.roles.commands.GiveMember;
import io.lightplugins.crit.modules.roles.commands.RemoveMember;
import io.lightplugins.crit.modules.roles.listener.RegisterCommands;
import io.lightplugins.crit.util.interfaces.LightModule;

public class LightRoles implements LightModule {

    @Override
    public void enable() {

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
        return false;
    }

    @Override
    public String getName() {
        return "LightRoles";
    }
}
