package com.vague.core.rag.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vague.core.rag.entity.ChunkBgeM3;
import com.vague.core.rag.mapper.ChunkBgeM3Mapper;
import com.vague.core.rag.service.ChunkBgeM3Service;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ChunkBgeM3ServiceImpl extends ServiceImpl<ChunkBgeM3Mapper, ChunkBgeM3> implements ChunkBgeM3Service {

    @Override
    public List<ChunkBgeM3> searchSimilar(String knowledgeBaseId, float[] embedding, int topK) {
        // float[] → "[1.0,2.0,3.0]" 的 pgvector 格式
        String vecStr = Arrays.toString(embedding).replace(" ", "");
        return baseMapper.searchByVector(knowledgeBaseId, vecStr, topK);
    }

    @Override
    public List<ChunkBgeM3> searchByVectorMultiKb(String kbIds, String embedding, int limit) {
        return baseMapper.searchByVectorMultiKb(kbIds, embedding, limit);
    }
}
