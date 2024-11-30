package com.wandr.backend.dao;

import com.wandr.backend.dto.chat.ChatMessageDTO;
import com.wandr.backend.entity.ChatMessage;
import com.wandr.backend.mapper.ChatMessageRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
public class ChatMessageDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageDAO.class);

    public ChatMessageDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(ChatMessage message) {
        logger.info("Sending message from " + message.getSenderId() + " to " + message.getReceiverId());
        String sql = "INSERT INTO chat_messages (sender_id, receiver_id, message, is_read, is_delivered) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, message.getSenderId(), message.getReceiverId(), message.getMessage(),
                message.getIsRead(), message.getIsDelivered());
    }

    public List<ChatMessage> getChatHistory(Long senderId, Long receiverId) {
        logger.info("Fetching chat history between " + senderId + " and " + receiverId);
        String sql = "SELECT * FROM chat_messages WHERE (sender_id = ? AND receiver_id = ?) " +
                "OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp";
        return jdbcTemplate.query(sql, new ChatMessageRowMapper(), senderId, receiverId, receiverId, senderId);
    }

    public void markAsRead(Long messageId) {
        logger.info("Marking message " + messageId + " as read");
        String sql = "UPDATE chat_messages SET is_read = TRUE WHERE message_id = ?";
        jdbcTemplate.update(sql, messageId);
    }

    public void markAsDelivered(Long messageId) {
        logger.info("Marking message " + messageId + " as delivered");
        String sql = "UPDATE chat_messages SET is_delivered = TRUE WHERE message_id = ?";
        jdbcTemplate.update(sql, messageId);
    }


    public ChatMessage findById(Long messageId) {
        logger.info("Fetching message with ID " + messageId);
        String sql = "SELECT * FROM chat_messages WHERE message_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new ChatMessageRowMapper(), messageId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
