package com.vague.core.chat.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话视图对象，用于会话列表 / 会话详情接口的返回
 */
@Data
public class ChatSessionVO {
    private String id;
    private String agentId;        // 当前会话绑定的 Agent
    private String title;          // 会话标题（自动生成或用户重命名）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
