package io.lightplugins.crit.modules.watchdog.logging;

import io.lightplugins.crit.modules.watchdog.LightWatchdog;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VoiceKick extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

        if (event.getChannelLeft() == null || event.getChannelJoined() != null) {
            return; // Only proceed if the user left a voice channel
        }

        long watchdogChannel = LightWatchdog.instance.getWatchdogConfig().getLong("logging.channelID");

        // The member who left the voice channel
        Member targetMember = event.getMember();
        String targetName = targetMember.getUser().getName();

        // Retrieve the member who kicked the user
        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs().type(ActionType.MEMBER_VOICE_KICK).limit(10);
        List<AuditLogEntry> logEntries = logs.complete();
        if (logEntries.isEmpty()) {
            LightPrinter.printError("No audit log entry found for voice kick.");
            return;
        }

        // Filter the audit log entries to find the one that matches the user who left the channel
        AuditLogEntry entry = logEntries.stream()
                .filter(logEntry -> logEntry.getTargetId().equals(targetMember.getId()))
                .findFirst()
                .orElse(null);

        if (entry == null) {
            LightPrinter.printError("No matching audit log entry found for user " + targetName);
            return;
        }
        User executorMember = entry.getUser();
        if (executorMember == null) {
            LightPrinter.printError("Executor not found in audit log entry.");
            return;
        }

        AudioChannel targetChannel = event.getChannelLeft();

        String executorName = executorMember.getName();
        TextChannel channel = event.getGuild().getTextChannelById(watchdogChannel);

        if (channel == null) {
            LightPrinter.printWatchdog("Watchdog channel not found. Please check watchdog.yml for the correct channel id.");
            return;
        }

        sendVoiceKickEmbed(channel, targetChannel,  executorName, targetName);
    }

    private void sendVoiceKickEmbed(TextChannel channel, AudioChannel targetChannel, String executor, String target) {
        String color = "F44336";
        String title = "Voice Channel Kick festgestellt";
        String url = "https://i.ibb.co/pdcPk8D/CRIT-E-Logo-2k-discord.png";
        String description = "Der Benutzer **#executor#** hat den user **#target#** aus dem Channel **#channel#** gekickt."
                .replace("#executor#", "**" + executor + "**")
                .replace("#target#", "**" + target + "**")
                .replace("#channel#", "**" + targetChannel.getName() + "**");
        String footer = "Bei Unregelmäßigkeiten bitte an einen Admin wenden.";

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(url);
        embedBuilder.setDescription(description);
        embedBuilder.setTitle(title);
        embedBuilder.addField("Wann ist das geschehen ?", localDateTime.format(formattedDate), false);
        embedBuilder.setColor(Color.decode("#" + color));
        embedBuilder.setFooter(footer);

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}