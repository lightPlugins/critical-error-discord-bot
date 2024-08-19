package io.lightplugins.crit.modules.profiles;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.modules.profiles.api.LightProfileAPI;
import io.lightplugins.crit.modules.profiles.commands.AddBirthdayCommand;
import io.lightplugins.crit.modules.profiles.listener.AddGuildMember;
import io.lightplugins.crit.modules.profiles.manager.BirthdayChecker;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.database.model.TableNames;
import io.lightplugins.crit.util.interfaces.LightModule;
import lombok.Getter;

    /**
     * Author: lightPlugins
     * Project: CriticalError Discord Bot
     * Date: 2023-10-05
     */

public class LightProfile implements LightModule {

    @Getter
    private static LightProfileAPI lightProfileAPI;
    private BirthdayChecker birthdayChecker;

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

        String channelId = "YOUR_CHANNEL_ID";
        this.birthdayChecker = new BirthdayChecker(lightProfileAPI, LightMaster.instance.getShardManager().getShardById(0), channelId);

        LightPrinter.print("LightProfile has been enabled.");

        LightMaster.instance.getDatabase()
                .createTable(TableNames.USER_DATA.getTableName(),
                        "uniqueId VARCHAR(36) PRIMARY KEY, " +
                                "username VARCHAR(128), " +
                                "ipAddress VARCHAR(100), " +
                                "lastSeen DATE, " +
                                "timeJoined VARCHAR(100), " +
                                "coins INT, " +
                                "birthday VARCHAR(100)"
                );

        LightMaster.instance.getShardManager().addEventListener(
                // single command classes
                new AddGuildMember(),
                new AddBirthdayCommand()
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
        return "LightProfile";
    }
}
