package com.vague.core.chat.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息视图对象，用于历史消息回显
 */
@Data
public class ChatMessageVO {
    private String id;
    private String role;           // user / assistant / system / tool
    private String content;
    private String metadata;       // JSONB 字符串，后续 RAG 命中片段会写到这里
    private LocalDateTime createdAt;
}
