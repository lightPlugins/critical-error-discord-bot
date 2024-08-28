package io.lightplugins.crit.modules.watchdog.logging;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.modules.profiles.api.LightProfileAPI;
import io.lightplugins.crit.modules.profiles.handler.ChatMessage;
import io.lightplugins.crit.modules.profiles.handler.MessageAttachment;
import io.lightplugins.crit.modules.watchdog.LightWatchdog;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DeletedMessages extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        long watchdogChannel = LightWatchdog.instance.getWatchdogConfig().getLong("logging.channelID");
        TextChannel logChannel = event.getGuild().getChannelById(TextChannel.class, watchdogChannel);
        if (logChannel == null) {
            LightPrinter.printError("Log channel not found");
            return;
        }

        MessageChannelUnion targetChannel = event.getChannel();
        String messageID = event.getMessageId();
        List<ChatMessage> messages = LightProfile.getLightProfileAPI().getMessages(messageID);
        List<MessageAttachment> attachments = LightProfile.getLightProfileAPI().getAttachments(messageID);

        if (!messages.isEmpty()) {
            ChatMessage message = messages.getLast(); // Assuming there's only one message per ID
            User author = event.getJDA().getUserById(message.getUserID());
            sendMessageDeleteEmbed(targetChannel, author, logChannel, message, attachments);
        } else {
            LightPrinter.printError("Deleted Message not found in database with id " + messageID);
        }
    }

    private void sendMessageDeleteEmbed(MessageChannelUnion targetChannel, User author, MessageChannel channel, ChatMessage message, List<MessageAttachment> attachments) {
        String color = "FF0000";
        String title = "Nachricht gelöscht";
        String description = "Eine Nachricht von #author# wurde in #channel# gelöscht."
                .replace("#author#", author.getAsMention())
                .replace("#channel#", targetChannel.getAsMention());
        String footer = "Bei Fragen bitte an einen Admin wenden. " +
                "Aufgrund der Limitierung der Discord API kann nicht angezeigt werde, wer die Nachricht gelöscht hat.";
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(author.getAvatarUrl());
        embedBuilder.setDescription(description);
        embedBuilder.setTitle(title);
        embedBuilder.addField("**Inhalt**", message.getMessageText(), false);
        embedBuilder.addField("Wann ist das geschehen?", localDateTime.format(formattedDate), false);
        embedBuilder.setColor(Color.decode("#" + color));
        embedBuilder.setFooter(footer);

        if (!attachments.isEmpty()) {
            StringBuilder attachmentsField = new StringBuilder();
            for (MessageAttachment attachment : attachments) {
                attachmentsField.append(attachment.getFilePath()).append("\n");
            }
            embedBuilder.addField("Anhänge", attachmentsField.toString(), false);
        }

        LightPrinter.print("Message from " + message.getUserID() + " deleted");

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}