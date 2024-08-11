package io.lightplugins.crit.modules.roles.commands;

import io.lightplugins.crit.enums.RoleDataPath;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.MessageSender;
import io.lightplugins.crit.util.PermissionNode;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RemoveMember extends ListenerAdapter {

    private final List<CommandData> commandData = new ArrayList<>();


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String executedCommand = event.getName();
        String triggerCommand = "removemember";

        // check if command is the trigger command
        if(!executedCommand.equalsIgnoreCase(triggerCommand)) {
            return;
        }

        // check if user has permission
        if(!PermissionNode.isSupporter(Objects.requireNonNull(event.getMember()))) {
            MessageSender.sendNoPermissionMessage(event);
            return;
        }

        // check if user is in the guild and valid
        OptionMapping userOption = event.getOption("user");
        if(userOption == null) {
            event.reply(":no_entry:  Missing requirements - Discord ID von User").setEphemeral(true).queue();
            return;
        }

        // Extract user ID from mention string
        String userIdString = userOption.getAsString().replaceAll("[^0-9]", "");
        long userId;
        try {
            userId = Long.parseLong(userIdString);
        } catch (NumberFormatException e) {
            event.reply(":no_entry:  Invalid user ID format.").setEphemeral(true).queue();
            return;
        }

        // get Member model from guild by ID
        Member user = Objects.requireNonNull(event.getGuild()).getMemberById(userId);

        if(user == null) {
            event.reply(":no_entry:  User **" + userOption.getAsString() +  "** wurde nicht gefunden.").
                    setEphemeral(true).queue();
            return;
        }

        if(user.getUser().isBot()) {
            event.reply(":no_entry:  Bots können nicht als Member hinzugefügt werden.").setEphemeral(true).queue();
            return;
        }

        // get role by ID
        Role memberRole = event.getGuild().getRoleById(RoleDataPath.MEMBER.getID());
        if (memberRole == null) {
            event.reply(":no_entry:  Member wurde nicht gefunden. Wende dich bitte an die IT-Technik")
                    .setEphemeral(true).queue();
            return;
        }

        // check if the user has the Member role
        if(!user.getRoles().contains(memberRole)) {
            event.reply(":no_entry:  User **" + user.getEffectiveName() + "** hat nicht den Member Rang.")
                    .setEphemeral(true).queue();
            return;
        }

        // add role to user
        event.getGuild().removeRoleFromMember(user, memberRole).queue();

        event.reply(":white_check_mark: Du hast dem User **" + user.getEffectiveName() + "** erfolgreich den Member Rang entfernt.")
                .setEphemeral(true).queue();
        LightPrinter.print("[COMMAND] " + event.getMember().getEffectiveName() + " has removed " + user.getNickname() + " the Member role.");
    }

}
