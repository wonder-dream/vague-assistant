package com.vague.core.rag.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.vague.core.chat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("knowledge_base")
public class KnowledgeBase extends BaseEntity {
    private String name;
    private String description;
    private String metadata;
}
