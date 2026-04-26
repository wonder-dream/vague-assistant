package com.vague.core.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vague.core.chat.entity.ChatMessage;
import com.vague.core.chat.mapper.ChatMessageMapper;
import com.vague.core.chat.service.ChatMessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Override
    public List<ChatMessage> getMessageHistory(String sessionId, int limit) {
        return this.lambdaQuery()
                .eq(ChatMessage::getSessionId, sessionId)   // 查询 sessionId 相同的
                .orderByAsc(ChatMessage::getCreatedAt)       // 根据创建时间正序排列
                .last("LIMIT " + limit)               // 只保留最近的 limit 个
                .list();
    }

    @Override
    public void insert(String sessionId, String role, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setRole(role);
        chatMessage.setContent(content);
        baseMapper.insert(chatMessage);
    }
}
