package io.lightplugins.crit.modules.watchdog.commands;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.MessageSender;
import io.lightplugins.crit.util.PermissionNode;
import io.lightplugins.crit.util.TimeFormatter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class ActiveTimeCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String executedCommand = event.getName();
        String triggerCommand = "timewaste";

        // check if command is the trigger command
        if (!executedCommand.equalsIgnoreCase(triggerCommand)) {
            return;
        }

        // check if user has permission
        if (PermissionNode.isAdmin(Objects.requireNonNull(event.getMember()))) {
            MessageSender.sendNoPermissionMessage(event);
            return;
        }

        // check if user is in the guild and valid
        OptionMapping userOption = event.getOption("user");
        if (userOption == null) {
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

        if (user == null) {
            event.reply(":no_entry:  User **" + userOption.getAsString() + "** wurde nicht gefunden.")
                    .setEphemeral(true).queue();
            return;
        }

        // Retrieve user profile
        UserProfile userProfile = LightProfile.getLightProfileAPI().getUserProfile(user.getId());
        if (userProfile == null) {
            event.reply(":no_entry:  User profile not found.").setEphemeral(true).queue();
            return;
        }
        // Format and display active times
        StringBuilder response = new StringBuilder("Aktivitäten von dem User **" + user.getEffectiveName() + "**");

        // add empty line
        response.append("\n\n");

        double overallTime = userProfile.getActiveTime().values().stream().mapToDouble(Double::doubleValue).sum();

        for (Map.Entry<String, Double> entry : userProfile.getActiveTime().entrySet()) {
            VoiceChannel channel = event.getGuild().getVoiceChannelById(entry.getKey());

            // Check if the channel is valid
            if(channel == null) {
                LightPrinter.print("Channel not found: " + entry.getKey());
                continue;
            }

            String formattedTime = TimeFormatter.formatTime(entry.getValue());

            response.append("Channel: ").append(channel.getName())
                    .append(" - Aktive Zeit: ").append(formattedTime).append("\n");
        }

        VoiceChannel afkChannel = event.getGuild().getAfkChannel();

        if(afkChannel == null) {
            LightPrinter.printError("AFK Channel not found while executing /timewaste. Please check the config.yml");
            return;
        }

        double afkTime = userProfile.getActiveTime().getOrDefault(afkChannel.getId(), 0.0);
        double timeSpentWithoutAfk = overallTime - afkTime;

        response.append("\nZeit in Voice Channels: (ohne AFK Zeiten) ")
                .append("**")
                .append(TimeFormatter.formatTime(timeSpentWithoutAfk))
                .append("**")
                .append("\nZeit im AFK Channel: ")
                .append("**")
                .append(TimeFormatter.formatTime(afkTime))
                .append("**")
                ;

        // send static message in channel
        // event.getChannel().sendMessage(response.toString()).queue();
        // send message only visible to the target user
        event.reply(response.toString()).setEphemeral(true).queue();

    }
}