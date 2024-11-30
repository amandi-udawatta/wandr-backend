package com.wandr.backend.mapper;

import com.wandr.backend.entity.ChatMessage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatMessageRowMapper implements RowMapper<ChatMessage> {

    @Override
    public ChatMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(rs.getLong("message_id"));
        chatMessage.setSenderId(rs.getLong("sender_id"));
        chatMessage.setReceiverId(rs.getLong("receiver_id"));
        chatMessage.setMessage(rs.getString("message"));
        chatMessage.setTimestamp(rs.getTimestamp("timestamp"));
        chatMessage.setIsRead(rs.getBoolean("is_read"));
        chatMessage.setIsDelivered(rs.getBoolean("is_delivered"));
        return chatMessage;
    }
}
