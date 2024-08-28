package io.lightplugins.crit.modules.profiles.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageAttachment {
    private String attachmentID;
    private String messageID;
    private String filePath;
    private String mediaType;

    public MessageAttachment(String attachmentID, String messageID, String filePath, String mediaType) {
        this.attachmentID = attachmentID;
        this.messageID = messageID;
        this.filePath = filePath;
        this.mediaType = mediaType;
    }

    // Konstruktor, Getter und Setter
}
