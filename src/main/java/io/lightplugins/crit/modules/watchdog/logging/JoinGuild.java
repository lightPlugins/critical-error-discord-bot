package io.lightplugins.crit.modules.watchdog.logging;

import io.lightplugins.crit.modules.message.LightMessage;
import io.lightplugins.crit.modules.watchdog.LightWatchdog;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;

public class JoinGuild extends ListenerAdapter {

    // add GuildMemberRoleAddEvent
    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {

        long watchdogChannel =
                LightWatchdog.instance.getWatchdogConfig().getLong("logging.channelID");

        // The member who received the role
        Member targetMember = event.getMember();
        String targetName = targetMember.getUser().getName();

        // The member who added the role (executor)
        User executorMember = event.getGuild()
                .retrieveAuditLogs()
                .type(ActionType.MEMBER_ROLE_UPDATE)
                .limit(1)
                .complete()
                .getFirst()
                .getUser();

        if(executorMember == null) {
            LightPrinter.printError("Audit log or User not found. Please check if the bot has the correct permissions.");
            return;
        }

        List<Role> role = event.getRoles();
        Guild guild = event.getGuild();
        TextChannel channel = guild.getTextChannelById(watchdogChannel);

        if(channel == null) {
            LightPrinter.printWatchdog(
                    "Watchdog channel not found. Please check watchdog.yml for the correct channel id.");
            return;
        }

        for (Role r : role) {
            roleAddEmbed(channel, executorMember, event.getMember().getUser(), r);
        }

    }

    // add roleRemove
    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {

        long watchdogChannel =
                LightWatchdog.instance.getWatchdogConfig().getLong("logging.channelID");

        // The member who received the role
        Member targetMember = event.getMember();
        String targetName = targetMember.getUser().getName();

        // The member who added the role (executor)
        User executorMember = event.getGuild()
                .retrieveAuditLogs()
                .type(ActionType.MEMBER_ROLE_UPDATE)
                .limit(1)
                .complete()
                .getFirst()
                .getUser();

        if(executorMember == null) {
            LightPrinter.printError("Audit log or User not found. Please check if the bot has the correct permissions.");
            return;
        }

        String executorName = executorMember.getName();
        List<Role> role = event.getRoles();
        Guild guild = event.getGuild();
        TextChannel channel = guild.getTextChannelById(watchdogChannel);

        if(channel == null) {
            LightPrinter.printWatchdog(
                    "Watchdog channel not found. Please check watchdog.yml for the correct channel id.");
            return;
        }

        for (Role r : role) {
            roleRemoveEmbed(channel, executorMember, targetMember.getUser(), r);
        }

    }

    // if a user leaves the guild
    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {

        long watchdogChannel =
                LightWatchdog.instance.getWatchdogConfig().getLong("logging.channelID");

        User user = event.getUser();
        Guild guild = event.getGuild();

        TextChannel channel = guild.getTextChannelById(watchdogChannel);

        if(channel == null) {
            LightPrinter.printWatchdog(
                    "Watchdog channel not found. Please check watchdog.yml for the correct channel id.");
            return;
        }

        removeUserEmbed(channel, user);


    }

    private void roleAddEmbed(TextChannel channel, User executor, User target, Role role) {

        String color = "32D732";
        String title = "Rollenänderung festgestellt";
        String url = "https://i.ibb.co/pdcPk8D/CRIT-E-Logo-2k-discord.png";
        String description = "Der Benutzer #executor# hat dem User #target# die Rolle #role# hinzugefügt."
                .replace("#executor#",  executor.getAsMention() + " (" + executor.getEffectiveName() + ")")
                .replace("#target#", target.getAsMention() + " (" + target.getEffectiveName() + ")")
                .replace("#role#", role.getAsMention() + " (" + role.getName() + ")");
        String footer = "Bei Unregelmäßigkeiten bitte an einen Admin wenden.";

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(url);
        embedBuilder.setDescription(description);
        embedBuilder.setTitle(title);
        embedBuilder.addField("Wann ist das geschehen ?", localDateTime.format(formatedDate), false);
        embedBuilder.setColor(HexFormat.fromHexDigits(color));
        embedBuilder.setFooter(footer);

        MessageEmbed roleAddEmbed = embedBuilder.build();
        channel.sendMessageEmbeds(roleAddEmbed).queue();

    }

    private void roleRemoveEmbed(TextChannel channel, User executor, User target, Role role) {

        String color = "F44336";
        String title = "Rollenänderung festgestellt";
        String url = "https://i.ibb.co/pdcPk8D/CRIT-E-Logo-2k-discord.png";
        String description = "Der Benutzer #executor# hat dem User #target# die Rolle #role# entfernt."
                .replace("#executor#", "**" + executor.getAsMention() + "** (" + executor.getEffectiveName() + ")")
                .replace("#target#", "**" + target.getAsMention() + "** (" + target.getEffectiveName() + ")")
                .replace("#role#", "**" + role.getAsMention() + "** (" + role.getName() + ")");
        String footer = "Bei Unregelmäßigkeiten bitte an einen Admin wenden.";

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(url);
        embedBuilder.setDescription(description);
        embedBuilder.setTitle(title);
        embedBuilder.addField("Wann ist das geschehen ?", localDateTime.format(formatedDate), false);
        embedBuilder.setColor(HexFormat.fromHexDigits(color));
        embedBuilder.setFooter(footer);

        MessageEmbed roleRemoveEmbed = embedBuilder.build();
        channel.sendMessageEmbeds(roleRemoveEmbed).queue();

    }

    private void removeUserEmbed(TextChannel channel, User user) {

        String color = "F44336";
        String title = "Server verlassen";
        String url = "https://i.ibb.co/pdcPk8D/CRIT-E-Logo-2k-discord.png";
        String description = "Der Benutzer #mention# hat den Server verlassen."
                .replace("#mention#", "**" + user.getAsMention() + "** (" + user.getEffectiveName() + ")");
        String footer = "Bei Unregelmäßigkeiten bitte an einen Admin wenden.";

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(url);
        embedBuilder.setDescription(description);
        embedBuilder.setTitle(title);
        embedBuilder.addField("Wann ist das geschehen ?", localDateTime.format(formatedDate), false);
        embedBuilder.setColor(HexFormat.fromHexDigits(color));
        embedBuilder.setFooter(footer);

        MessageEmbed roleRemoveEmbed = embedBuilder.build();
        channel.sendMessageEmbeds(roleRemoveEmbed).queue();

    }
}
