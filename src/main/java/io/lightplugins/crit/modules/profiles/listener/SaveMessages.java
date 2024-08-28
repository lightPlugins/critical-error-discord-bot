package io.lightplugins.crit.modules.profiles.listener;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Timestamp;
import java.util.List;

public class SaveMessages extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        LightPrinter.printWatchdog("MessageReceivedEvent has been triggered.");
        Message message = event.getMessage();

        // check if the author is a bot and cancel the save process
        if (message.getAuthor().isBot()) {
            LightPrinter.printDebug("Message author is a bot. Canceling save process.");
            return;
        }

        String messageID = message.getId();
        String userID = message.getAuthor().getId();
        String messageText = message.getContentRaw();
        Timestamp timestamp = new Timestamp(message.getTimeCreated().toInstant().toEpochMilli());

        LightProfile.getLightProfileAPI().saveMessage(messageID, userID, messageText, timestamp);

        List<Attachment> attachments = message.getAttachments();
        LightPrinter.printDebug("Found " + attachments.size() + " attachments in the message.");
        for (Attachment attachment : attachments) {
            LightPrinter.printDebug("Attachment ID: " + attachment.getId());
            LightPrinter.printDebug("Attachment URL: " + attachment.getUrl());
            LightPrinter.printDebug("Content Type: " + attachment.getContentType());
            String attachmentID = attachment.getId();
            String filePath = attachment.getUrl();
            String mediaType = attachment.getContentType();
            LightProfile.getLightProfileAPI().saveAttachment(attachmentID, messageID, userID, filePath, mediaType);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {

        LightPrinter.printWatchdog("MessageUpdateEvent has been triggered.");
        Message message = event.getMessage();

        // check if the author is a bot and cancel the save process
        if (message.getAuthor().isBot()) {
            LightPrinter.printDebug("Message author is a bot. Canceling save process.");
            return;
        }

        String messageID = message.getId();
        String userID = message.getAuthor().getId();
        String messageText = message.getContentRaw();

        if(message.getTimeEdited() == null) {
            LightPrinter.printError("Message time edited is null. Could not save updated Message to database.");
            return;
        }

        Timestamp timestamp = new Timestamp(message.getTimeEdited().toInstant().toEpochMilli());
        LightProfile.getLightProfileAPI().saveMessage(messageID, userID, messageText, timestamp);

        List<Attachment> attachments = message.getAttachments();
        LightPrinter.printDebug("Found " + attachments.size() + " attachments in the message.");
        for (Attachment attachment : attachments) {
            LightPrinter.printDebug("Attachment ID: " + attachment.getId());
            LightPrinter.printDebug("Attachment URL: " + attachment.getUrl());
            LightPrinter.printDebug("Content Type: " + attachment.getContentType());
            String attachmentID = attachment.getId();
            String filePath = attachment.getUrl();
            String mediaType = attachment.getContentType();
            LightProfile.getLightProfileAPI().saveAttachment(attachmentID, messageID, userID, filePath, mediaType);
        }
    }
}
