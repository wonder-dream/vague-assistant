package com.vague.core.rag.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeBaseVO {
    private String id;
    private String name;
    private String description;
    private String metadata;
    private LocalDateTime createdAt;
}
