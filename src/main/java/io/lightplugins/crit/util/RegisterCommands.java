package io.lightplugins.crit.util;

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

    private final List<CommandData> commandData = new ArrayList<>();

    public void onGuildReady(@NotNull GuildReadyEvent event) {

        // Avoid duplicated command entry error on internet reconnection
        if(LightMaster.instance.isStartupComplete()) {
            return;
        }

        /*
         *  Register OptionData for any commands that need them
         *
         */

        // options from /addbirthday
        OptionData addBirthday = new OptionData(OptionType.STRING, "birthday",
                "Dein Geburtstag. Zum Beispiel 02.03.2000", true);
        // options from /addmember
        OptionData giveMemberOption = new OptionData(OptionType.STRING, "user",
                "Discord @ Tag des Users", true);
        // options from /removemember
        OptionData removeMemberOption = new OptionData(OptionType.STRING, "user",
                "Discord @ Tag des Users", true);



        /*
         *  Add all commands to the commandData list
         *
         */

        // add command /addbirthday to the list
        commandData.add(Commands.slash("addbirthday",
                "Trägt dich in die Geburtstagsliste ein").addOptions(addBirthday));
        // add command /addmember to the list
        commandData.add(Commands.slash("addmember",
                "Gibt einem User die Member Rolle").addOptions(giveMemberOption));
        // add command /removemember to the list
        commandData.add(Commands.slash("removemember",
                "Gibt einem User die Member Rolle").addOptions(removeMemberOption));


        /*
         *  Finally, register the commands to the guild
         *
         */

        event.getGuild().updateCommands().addCommands(commandData).queue(
                callback -> LightPrinter.print("Successfully registered all the commands"));

        LightMaster.instance.setStartupComplete(true);
    }
}
