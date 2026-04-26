package com.vague.core.chat.entity.dto;

import lombok.Data;

@Data
public class AgentCreateRequest {
    private String name;            // agent 名称
    private String description;     // agent 描述信息
    private String systemPrompt;    // agent 系统提示词
    private String model;           // agent 使用的模型
    private String allowedTools;    // agent 允许使用的工具列表
    private String allowedKbs;      // agent 允许使用的知识库列表
    private String chatOptions;     // agent 聊天相关设置
}
