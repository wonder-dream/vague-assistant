package com.vague.core.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vague.core.chat.entity.ChatSession;
import com.vague.core.chat.entity.dto.ChatRequestMessage;
import com.vague.core.chat.mapper.ChatSessionMapper;
import com.vague.core.chat.service.ChatSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
public class ChatSessionServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatSessionService {

    @Override
    public String createSession(ChatRequestMessage chatRequestMessage) {

        String content = chatRequestMessage.getContent();

        // 创建 Session
        ChatSession chatSession = new ChatSession();
        chatSession.setAgentId(chatRequestMessage.getAgentId());
        chatSession.setTitle(generateTitle(content));
        baseMapper.insert(chatSession);
        return chatSession.getId();
    }

    private String generateTitle(String content) {
        // 生成会话标题
        if (content == null || content.trim().isEmpty()) {
            return "新会话";
        }
        String trim =  content.trim();
        if (trim.length() < 10) {
            return trim;
        }
        return trim.substring(0, 10) + "...";
    }
}
