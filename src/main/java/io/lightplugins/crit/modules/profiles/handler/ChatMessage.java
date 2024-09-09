package io.lightplugins.crit.modules.profiles.handler;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ChatMessage {
    private String messageID;
    private String userID;
    private String messageText;
    private Timestamp timestamp;

    public ChatMessage(String messageID, String userID, String messageText, Timestamp timestamp) {
        this.messageID = messageID;
        this.userID = userID;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // Konstruktor, Getter und Setter
}
