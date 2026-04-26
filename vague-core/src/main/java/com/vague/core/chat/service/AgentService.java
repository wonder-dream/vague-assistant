package com.vague.core.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vague.core.chat.entity.Agent;

import java.util.List;

public interface AgentService extends IService<Agent> {
    String getSystemPrompt(String agentId);

    void switchSessionAgent(String sessionId, String agentId);

    List<String> getAllowedKbIds(String agentId);
}
