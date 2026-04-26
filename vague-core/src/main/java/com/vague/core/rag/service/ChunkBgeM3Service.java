package com.vague.core.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vague.core.rag.entity.ChunkBgeM3;

import java.util.List;

public interface ChunkBgeM3Service extends IService<ChunkBgeM3> {
    /**
     * 向量检索：将 float[] 自动转为 pgvector 字符串格式查询
     */
    List<ChunkBgeM3> searchSimilar(String knowledgeBaseId, float[] embedding, int topK);

    List<ChunkBgeM3> searchByVectorMultiKb(String kbIds, String embedding, int limit);
}
