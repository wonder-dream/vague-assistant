package com.vague.core.rag.embedding;

public interface EmbeddingClient {

    /**
     * 文本向量化
     * @param text 输入文本
     * @return float[] 向量数组（bge-m3 为 1024 维）
     */
    float[] embed(String text);
}