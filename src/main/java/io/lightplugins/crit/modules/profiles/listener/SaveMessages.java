package io.lightplugins.crit.modules.profiles.listener;

import io.lightplugins.crit.modules.profiles.LightProfile;
import io.lightplugins.crit.util.LightPrinter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SaveMessages extends ListenerAdapter {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 2; // in seconds
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Message message = event.getMessage();

        // check if the author is a bot and cancel the save process
        if (message.getAuthor().isBot()) {
            LightPrinter.printDebug("Message author is a bot. Canceling save process.");
            return;
        }

        LightPrinter.printWatchdog("MessageReceivedEvent has been triggered.");

        String messageID = message.getId();
        String userID = message.getAuthor().getId();
        String messageText = message.getContentRaw();
        Timestamp timestamp = new Timestamp(message.getTimeCreated().toInstant().toEpochMilli());

        saveMessageWithRetry(messageID, userID, messageText, timestamp, MAX_RETRIES);

        List<Attachment> attachments = message.getAttachments();
        LightPrinter.printDebug("Found " + attachments.size() + " attachments in the message.");
        for (Attachment attachment : attachments) {
            LightPrinter.printDebug("Attachment ID: " + attachment.getId());
            LightPrinter.printDebug("Attachment URL: " + attachment.getUrl());
            LightPrinter.printDebug("Content Type: " + attachment.getContentType());
            String attachmentID = attachment.getId();
            String filePath = attachment.getUrl();
            String mediaType = attachment.getContentType();
            saveAttachmentWithRetry(attachmentID, messageID, userID, filePath, mediaType, MAX_RETRIES);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        Message message = event.getMessage();

        // check if the author is a bot and cancel the save process
        if (message.getAuthor().isBot()) {
            LightPrinter.printDebug("Message author is a bot. Canceling save process.");
            return;
        }

        LightPrinter.printWatchdog("MessageUpdateEvent has been triggered.");

        String messageID = message.getId();
        String userID = message.getAuthor().getId();
        String messageText = message.getContentRaw();

        if (message.getTimeEdited() == null) {
            LightPrinter.printError("Could not reach Discord API while trying to get the edited time of the message.");
            LightPrinter.printWatchdog("Lets try it again in " + RETRY_DELAY + " second(s).");
            scheduleRetry(() -> onMessageUpdate(event));
            return;
        }

        Timestamp timestamp = new Timestamp(message.getTimeEdited().toInstant().toEpochMilli());
        saveMessageWithRetry(messageID, userID, messageText, timestamp, MAX_RETRIES);

        List<Attachment> attachments = message.getAttachments();
        LightPrinter.printDebug("Found " + attachments.size() + " attachments in the message.");
        for (Attachment attachment : attachments) {
            LightPrinter.printDebug("Attachment ID: " + attachment.getId());
            LightPrinter.printDebug("Attachment URL: " + attachment.getUrl());
            LightPrinter.printDebug("Content Type: " + attachment.getContentType());
            String attachmentID = attachment.getId();
            String filePath = attachment.getUrl();
            String mediaType = attachment.getContentType();
            saveAttachmentWithRetry(attachmentID, messageID, userID, filePath, mediaType, MAX_RETRIES);
        }
    }

    private void saveMessageWithRetry(String messageID, String userID, String messageText, Timestamp timestamp, int retries) {
        try {
            LightProfile.getLightProfileAPI().saveMessage(messageID, userID, messageText, timestamp);
            LightPrinter.print("Message with ID " + messageID + " saved successfully.");
        } catch (ErrorResponseException e) {
            LightPrinter.printWatchdog("Try " + (MAX_RETRIES - retries + 1) + " to save the message.");
            if (e.getErrorCode() == 503 && retries > 0) {
                scheduleRetry(() -> saveMessageWithRetry(messageID, userID, messageText, timestamp, retries - 1));
            } else {
                LightPrinter.printError("Failed to save message with " + 3 + "tries: " + e.getMessage());
            }
        }
    }

    private void saveAttachmentWithRetry(String attachmentID, String messageID, String userID, String filePath, String mediaType, int retries) {
        try {
            LightProfile.getLightProfileAPI().saveAttachment(attachmentID, messageID, userID, filePath, mediaType);
            LightPrinter.print("Attachment with ID " + attachmentID + " saved successfully.");
        } catch (ErrorResponseException e) {
            LightPrinter.printWatchdog("Try " + (MAX_RETRIES - retries + 1) + " to save the message.");
            if (e.getErrorCode() == 503 && retries > 0) {
                scheduleRetry(() -> saveAttachmentWithRetry(attachmentID, messageID, userID, filePath, mediaType, retries - 1));
            } else {
                LightPrinter.printError("Failed to save message with " + 3 + "tries: " + e.getMessage());
            }
        }
    }

    private void scheduleRetry(Runnable task) {
        scheduler.schedule(task, RETRY_DELAY, TimeUnit.SECONDS);
    }
}