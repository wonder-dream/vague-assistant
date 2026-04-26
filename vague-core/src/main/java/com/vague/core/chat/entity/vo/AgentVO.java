package com.vague.core.chat.entity.vo;

import lombok.Data;

@Data
public class AgentVO {
    private String id;
    private String name;
    private String description;
    private String systemPrompt;
    private String model;
}
