package io.lightplugins.crit.commands;

import io.lightplugins.crit.master.LightMaster;
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

    public void onGuildReady(@NotNull GuildReadyEvent event) {

        List<CommandData> commandData = new ArrayList<>();

        OptionData optionData1 = new OptionData(OptionType.STRING, "frage",
                "Add coins to a specify user", true);

        commandData.add(Commands.slash("umfrage",
                "Create a Pool with an specified question").addOptions(optionData1));


        event.getGuild().updateCommands().addCommands(commandData).queue(
                callback -> System.out.println(LightMaster.getPrefix() + "Successfully registered command 'umfrage'"));


    }
}
