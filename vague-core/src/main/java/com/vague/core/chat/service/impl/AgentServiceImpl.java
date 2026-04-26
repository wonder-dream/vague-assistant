package com.vague.core.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vague.core.chat.common.exception.BusinessException;
import com.vague.core.chat.entity.Agent;
import com.vague.core.chat.entity.ChatSession;
import com.vague.core.chat.mapper.AgentMapper;
import com.vague.core.chat.service.AgentService;
import com.vague.core.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl extends ServiceImpl<AgentMapper, Agent> implements AgentService {

    private final ChatSessionService chatSessionService;
    private final Gson gson;

    @Value("${deepseek.default-system-prompt:You are a helpful assistant.}")
    private String defaultSystemPrompt;

    @Override
    public String getSystemPrompt(String agentId) {
        if (agentId == null) {
            return defaultSystemPrompt;
        }

        Agent agent = this.getById(agentId);

        if (agent == null) {
            return defaultSystemPrompt;
        }

        String prompt = agent.getSystemPrompt();
        return prompt != null ? prompt : defaultSystemPrompt;
    }

    @Override
    public void switchSessionAgent(String sessionId, String agentId) {
        ChatSession session = chatSessionService.getById(sessionId);
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        if (getById(agentId) == null) {
            throw new BusinessException(404, "Agent 不存在");
        }
        session.setAgentId(agentId);
        chatSessionService.updateById(session);
    }

    @Override
    public List<String> getAllowedKbIds(String agentId) {
        if (agentId == null) return List.of();
        Agent agent = this.getById(agentId);
        if (agent == null || agent.getAllowedKbs() == null) return List.of();
        return gson.fromJson(agent.getAllowedKbs(), new TypeToken<List<String>>() {}.getType());
    }
}
