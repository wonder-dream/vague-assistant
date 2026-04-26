package com.vague.core.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vague.core.chat.entity.ChatSession;
import com.vague.core.chat.entity.dto.ChatRequestMessage;

public interface ChatSessionService extends IService<ChatSession> {
    String createSession(ChatRequestMessage chatRequestMessage);
}
