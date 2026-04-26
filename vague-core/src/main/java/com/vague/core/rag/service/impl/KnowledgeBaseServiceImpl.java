package com.vague.core.rag.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vague.core.rag.entity.KnowledgeBase;
import com.vague.core.rag.entity.dto.KnowledgeBaseCreateRequest;
import com.vague.core.rag.mapper.KnowledgeBaseMapper;
import com.vague.core.rag.service.KnowledgeBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase>
        implements KnowledgeBaseService {
    @Override
    public String create(KnowledgeBaseCreateRequest request) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(request, knowledgeBase);
        save(knowledgeBase);
        return knowledgeBase.getId();
    }
}
