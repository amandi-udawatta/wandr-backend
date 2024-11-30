package com.wandr.backend.service.impl;

import com.wandr.backend.dao.ChatMessageDAO;
import com.wandr.backend.dto.chat.ChatMessageDTO;
import com.wandr.backend.entity.ChatMessage;
import com.wandr.backend.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageDAO chatMessageDAO;

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    @Autowired
    public ChatMessageServiceImpl(ChatMessageDAO chatMessageDAO) {
        this.chatMessageDAO = chatMessageDAO;
    }

    @Override
    public void sendMessage(ChatMessageDTO messageDTO) {
        logger.info("Sending message from " + messageDTO.getSenderId() + " to " + messageDTO.getReceiverId());
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(messageDTO.getSenderId());
        chatMessage.setReceiverId(messageDTO.getReceiverId());
        chatMessage.setMessage(messageDTO.getMessage());
        chatMessage.setTimestamp(new Timestamp(System.currentTimeMillis()));
        chatMessage.setIsRead(false);
        chatMessage.setIsDelivered(false);
        chatMessageDAO.save(chatMessage);
        logger.info("Message sent successfully");
    }

    @Override
    public List<ChatMessageDTO> getChatHistory(Long senderId, Long receiverId) {
        logger.info("Fetching chat history between " + senderId + " and " + receiverId);
        return chatMessageDAO.getChatHistory(senderId, receiverId).stream().map(chatMessage -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setMessageId(chatMessage.getMessageId());
            dto.setSenderId(chatMessage.getSenderId());
            dto.setReceiverId(chatMessage.getReceiverId());
            dto.setMessage(chatMessage.getMessage());
            dto.setTimestamp(chatMessage.getTimestamp().toString());
            dto.setIsRead(chatMessage.getIsRead());
            dto.setIsDelivered(chatMessage.getIsDelivered());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void markAsDelivered(Long messageId) {
        logger.info("Marking message " + messageId + " as delivered");
        chatMessageDAO.markAsDelivered(messageId);
    }

    @Override
    public void markAsRead(Long messageId) {
        logger.info("Marking message " + messageId + " as read");
        chatMessageDAO.markAsRead(messageId);
    }

    @Override
    public ChatMessageDTO getMessageById(Long messageId) {
        logger.info("Fetching message with ID " + messageId);
        ChatMessage chatMessage = chatMessageDAO.findById(messageId);
        if (chatMessage == null) {
            return null;
        }
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setMessageId(chatMessage.getMessageId());
        dto.setSenderId(chatMessage.getSenderId());
        dto.setReceiverId(chatMessage.getReceiverId());
        dto.setMessage(chatMessage.getMessage());
        dto.setTimestamp(chatMessage.getTimestamp().toString());
        dto.setIsRead(chatMessage.getIsRead());
        dto.setIsDelivered(chatMessage.getIsDelivered());
        return dto;
    }
}
