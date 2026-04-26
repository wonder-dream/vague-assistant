package com.vague.core.chat.entity.dto;

import lombok.Data;

@Data
public class SwitchAgentDTO {
    private String sessionId;
    private String agentId;
}
