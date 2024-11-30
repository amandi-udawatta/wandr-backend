package com.wandr.backend.entity;

import lombok.*;

import java.sql.Timestamp;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessage {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String message; // Optionally encrypted
    private Timestamp timestamp;
    private Boolean isRead;
    private Boolean isDelivered;
}
