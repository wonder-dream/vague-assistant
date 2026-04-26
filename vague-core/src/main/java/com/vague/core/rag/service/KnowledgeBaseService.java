package com.vague.core.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vague.core.rag.entity.KnowledgeBase;
import com.vague.core.rag.entity.dto.KnowledgeBaseCreateRequest;

public interface KnowledgeBaseService extends IService<KnowledgeBase> {
    String create(KnowledgeBaseCreateRequest request);
}
