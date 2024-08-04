package io.lightplugins.crit.modules.roles.listener;

import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RegisterCommands extends ListenerAdapter {

    private final List<CommandData> commandData = new ArrayList<>();

    public void onGuildReady(@NotNull GuildReadyEvent event) {

        // options from the target command
        OptionData giveMemberOption = new OptionData(OptionType.STRING, "user",
                "Discord @ Tag des Users", true);
        OptionData removeMemberOption = new OptionData(OptionType.STRING, "user",
                "Discord @ Tag des Users", true);

        // register command and add options
        commandData.add(Commands.slash("addmember",
                "Gibt einem User die Member Rolle").addOptions(giveMemberOption));
        commandData.add(Commands.slash("removemember",
                "Gibt einem User die Member Rolle").addOptions(removeMemberOption));

        event.getGuild().updateCommands().addCommands(commandData).queue(
                callback -> LightPrinter.print("Successfully registered commands from module 'LightRoles'"));


    }

}
