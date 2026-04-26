package com.vague.core.chat.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestMessage {
    private String sessionId;       // 会话 ID
    private String agentId;         // agent ID
    private String content;         // 正文内容
}
