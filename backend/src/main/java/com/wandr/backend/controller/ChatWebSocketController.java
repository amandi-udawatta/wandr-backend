package com.wandr.backend.controller;

import com.wandr.backend.dto.chat.ChatMessageDTO;
import com.wandr.backend.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatMessageService chatMessageService;

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    @MessageMapping("/chat") // Clients send messages to /app/chat
    @SendTo("/topic/messages") // Server broadcasts messages to /topic/messages
    public ChatMessageDTO sendMessage(ChatMessageDTO messageDTO) {
        System.out.println("sender: " + messageDTO.getSenderId());
        logger.info("Sending message from " + messageDTO.getSenderId() + " to " + messageDTO.getReceiverId());
        chatMessageService.sendMessage(messageDTO);
        return messageDTO; // Broadcast the message to all subscribers
    }

    @MessageMapping("/chat/delivered") // Clients send delivery updates to /app/chat/delivered
    public void markAsDelivered(ChatMessageDTO messageDTO) {
        logger.info("Marking message " + messageDTO.getMessageId() + " as delivered");
        chatMessageService.markAsDelivered(messageDTO.getMessageId());
    }

    @MessageMapping("/chat/read") // Clients send read updates to /app/chat/read
    public void markAsRead(ChatMessageDTO messageDTO) {
        logger.info("Marking message " + messageDTO.getMessageId() + " as read");
        chatMessageService.markAsRead(messageDTO.getMessageId());
    }
}
