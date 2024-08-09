package io.lightplugins.crit.modules.profiles;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.profiles.api.LightProfileAPI;
import io.lightplugins.crit.modules.profiles.listener.AddGuildMember;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.model.TableNames;
import io.lightplugins.crit.util.interfaces.LightModule;
import lombok.Getter;

public class LightProfile implements LightModule {

    @Getter
    private static LightProfileAPI lightProfileAPI;

    @Override
    public void enable() {

        /*  template for register events

        shardManager.addEventListener(
                new LoggerEvent(),
                new PollCommand(),
                new ReactionRoles(),
                new RegisterCommands());
         */

        lightProfileAPI = new LightProfileAPI();

        LightPrinter.print("LightProfile has been enabled.");

        LightMaster.instance.getDatabase()
                .createTable(TableNames.USER_DATA.getTableName(),
                        "uniqueId VARCHAR(36) PRIMARY KEY, " +
                                "username VARCHAR(128), " +
                                "ipAddress VARCHAR(100), " +
                                "lastSeen DATE, " +
                                "coins INT, " +
                                "birthday DATE"
                );

        LightMaster.instance.getShardManager().addEventListener(new AddGuildMember());
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
        return "LightProfile";
    }
}
