package com.wandr.backend.service;

import com.wandr.backend.dto.chat.ChatMessageDTO;

import java.util.List;

public interface ChatMessageService {
    void sendMessage(ChatMessageDTO messageDTO);

    List<ChatMessageDTO> getChatHistory(Long senderId, Long receiverId);

    void markAsDelivered(Long messageId);

    void markAsRead(Long messageId);

    ChatMessageDTO getMessageById(Long messageId);

}
