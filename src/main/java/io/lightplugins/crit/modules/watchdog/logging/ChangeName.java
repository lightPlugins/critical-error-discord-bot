package io.lightplugins.crit.modules.watchdog.logging;

import io.lightplugins.crit.modules.watchdog.LightWatchdog;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChangeName extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        long watchdogChannel = LightWatchdog.instance.getWatchdogConfig().getLong("logging.channelID");

        // The member who changed their nickname
        User user = event.getMember().getUser();
        String oldNickname = event.getOldNickname();
        String newNickname = event.getNewNickname();

        if(oldNickname == null) {
            oldNickname = "Kein Server Nickname gefunden";
        }

        TextChannel channel = event.getGuild().getTextChannelById(watchdogChannel);

        if (channel == null) {
            LightPrinter.printWatchdog("Watchdog channel not found. Please check watchdog.yml for the correct channel id.");
            return;
        }

        sendNicknameChangeEmbed(channel, user, oldNickname, newNickname);
    }

    private void sendNicknameChangeEmbed(TextChannel channel, User user, String oldNickname, String newNickname) {
        String color = "2196F3";
        String title = "Namensänderung festgestellt";
        String url = "https://i.ibb.co/pdcPk8D/CRIT-E-Logo-2k-discord.png";
        String description = "Der Benutzer **#member#** hat seinen Namen von **#oldNickname#** zu **#newNickname#** geändert."
                .replace("#member#",  user.getAsMention() + " (" + user.getEffectiveName() + ")")
                .replace("#oldNickname#", "**" + oldNickname + "**")
                .replace("#newNickname#", "**" + newNickname + "**");
        String footer = "Bei Unregelmäßigkeiten bitte an einen Admin wenden.";

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setDescription(description);
        embedBuilder.setTitle(title);
        embedBuilder.addField("Wann ist das geschehen?", localDateTime.format(formattedDate), false);
        embedBuilder.setColor(Color.decode("#" + color));
        embedBuilder.setFooter(footer);

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}