package io.lightplugins.crit.modules.watchdog.logging;

import io.lightplugins.crit.modules.watchdog.LightWatchdog;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DeletedMessages extends ListenerAdapter {

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        long watchdogChannel = LightWatchdog.instance.getWatchdogConfig().getLong("logging.channelID");
        TextChannel logChannel = event.getGuild().getChannelById(TextChannel.class, watchdogChannel);
        if (logChannel == null) {
            LightPrinter.printError("Log channel not found");
            return;
        }

        Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
        sendMessageDeleteEmbed(logChannel, message);
    }

    private void sendMessageDeleteEmbed(MessageChannel channel, Message message) {
        String color = "FF0000";
        String title = "Nachricht gelöscht";
        String description = "Eine Nachricht von **#author#** wurde gelöscht."
                .replace("#author#", message.getAuthor().getAsTag());
        String footer = "Bei Fragen bitte an einen Admin wenden.";

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(description);
        embedBuilder.setTitle(title);
        embedBuilder.addField("Inhalt", message.getContentDisplay(), false);
        embedBuilder.addField("Wann ist das geschehen?", localDateTime.format(formattedDate), false);
        embedBuilder.setColor(Color.decode("#" + color));
        embedBuilder.setFooter(footer);

        if (!message.getAttachments().isEmpty()) {
            StringBuilder attachments = new StringBuilder();
            for (Message.Attachment attachment : message.getAttachments()) {
                attachments.append(attachment.getUrl()).append("\n");
            }
            embedBuilder.addField("Anhänge", attachments.toString(), false);
        }

        LightPrinter.print("Message from " + message.getAuthor().getName() + " deleted");

        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}