package com.wandr.backend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ChatMessageDTO {
    private Long messageId;       // Unique ID for the message
    private Long senderId;        // ID of the sender
    private Long receiverId;      // ID of the receiver
    private String message;       // The message content
    private String timestamp;     // Timestamp in a user-readable format
    private Boolean isRead;       // Whether the message has been read
    private Boolean isDelivered;  // Whether the message has been delivered
}
