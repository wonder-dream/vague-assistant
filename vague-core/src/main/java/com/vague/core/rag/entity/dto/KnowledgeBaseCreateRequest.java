package com.vague.core.rag.entity.dto;

import lombok.Data;

@Data
public class KnowledgeBaseCreateRequest {
    private String name;
    private String description;
    private String metadata;
}
