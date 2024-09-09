package io.lightplugins.crit.modules.watchdog.commands;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.modules.profiles.impl.UserProfile;
import io.lightplugins.crit.util.LightPrinter;
import io.lightplugins.crit.util.MessageSender;
import io.lightplugins.crit.util.PermissionNode;
import io.lightplugins.crit.util.TimeFormatter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ActiveTimeCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        String executedCommand = event.getName();
        String triggerCommand = "timewaste";
        Member user;
        Guild guild = event.getGuild();

        // check if command is the trigger command
        if (!executedCommand.equalsIgnoreCase(triggerCommand)) {
            return;
        }

        if(event.getMember() == null) {
            LightPrinter.printError("Member not found while executing /timewaste");
            return;
        }

        // check if user is in the guild and valid / existing options
        OptionMapping userOption = event.getOption("user");
        boolean withoutOptions = userOption == null;

        // get Member model from guild by ID or without options (own id)
        if(withoutOptions) {
            user = event.getMember();
        } else {
            // check if the user has the permission to show the activity of another user
            if(!PermissionNode.isMember(event.getMember())) {
                MessageSender.sendNoPermissionMessage(event);
                return;
            }

            // Extract user ID from mention string / option
            String userIdString = userOption.getAsString().replaceAll("[^0-9]", "");
            long userId;
            try {
                userId = Long.parseLong(userIdString);
            } catch (NumberFormatException e) {
                event.reply(":no_entry:  Invalid user ID format.").setEphemeral(true).queue();
                return;
            }

            user = Objects.requireNonNull(event.getGuild()).getMemberById(userId);

            if (user == null) {
                event.reply(":no_entry:  User **" + userOption.getAsString() + "** wurde nicht gefunden.")
                        .setEphemeral(true).queue();
                return;
            }
        }

        // Retrieve user profile
        UserProfile userProfile = LightProfile.getLightProfileAPI().getUserProfile(user.getId());
        if (userProfile == null) {
            event.reply(":no_entry:  User profile not found.").setEphemeral(true).queue();
            return;
        }

        // Check if guild is valid
        if(guild == null) {
            LightPrinter.printError("Guild not found while executing /timewaste");
            return;
        }

        // Format and display active times
        StringBuilder response = new StringBuilder("Aktivitäten von dem User **" + user.getEffectiveName() + "**");

        // add empty line
        response.append("\n\n");

        double overallTime = userProfile.getActiveTime().values().stream().mapToDouble(Double::doubleValue).sum();
        AtomicInteger counter = new AtomicInteger();
        // Sort active times by duration in descending order
        userProfile.getActiveTime().entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {

                    VoiceChannel channel = guild.getVoiceChannelById(entry.getKey());
                    counter.getAndIncrement();
                    // Check if the channel is valid
                    if (channel == null) {
                        LightPrinter.print("Channel not found: " + entry.getKey());
                        return;
                    }

                    String formattedTime = TimeFormatter.formatTime(entry.getValue());

                    // Append channel name and active time to the response
                    response.append("#")
                            .append(counter.get())
                            .append(". ");
                    response.append(channel.getAsMention())
                            .append(" ")
                            .append("- Aktive Zeit: ").append(formattedTime).append("\n");
                });

        VoiceChannel afkChannel = guild.getAfkChannel();

        if (afkChannel == null) {
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
                .append("**");

        // send static message in channel
        // event.getChannel().sendMessage(response.toString()).queue();
        // send message only visible to the target user
        event.reply(response.toString()).setEphemeral(true).queue();
    }
}