package com.vague.core.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vague.core.chat.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
    List<ChatMessage> getMessageHistory(String sessionId, int limit);

    void insert(String sessionId, String role, String content);
}
