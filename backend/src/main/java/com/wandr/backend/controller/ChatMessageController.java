package com.wandr.backend.controller;

import com.wandr.backend.dto.ApiResponse;
import com.wandr.backend.dto.chat.ChatMessageDTO;
import com.wandr.backend.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageController.class);

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatMessageDTO>>> getChatHistory(
            @RequestParam Long senderId,
            @RequestParam Long receiverId) {
        logger.info("Fetching chat history between " + senderId + " and " + receiverId);
        try {
            List<ChatMessageDTO> messages = chatMessageService.getChatHistory(senderId, receiverId);
            return ResponseEntity.ok(new ApiResponse<>(true, 200, "Chat history fetched successfully", messages));
        } catch (Exception e) {
            logger.error("Error fetching chat history: {}", e.getMessage(), e);
            return ResponseEntity.ok(new ApiResponse<>(false, 400, "Error fetching chat history"));
        }
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<ChatMessageDTO> getMessage(@PathVariable Long messageId) {
        logger.info("Fetching message with ID " + messageId);
        try{
            ChatMessageDTO message = chatMessageService.getMessageById(messageId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            logger.error("Error fetching message: {}", e.getMessage(), e);
            return ResponseEntity.ok(null);
        }
    }
}
