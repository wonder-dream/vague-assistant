package com.vague.core.rag.embedding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    /** Ollama 服务地址 */
    private String ollamaBaseUrl = "http://localhost:11434";

    /** 向量化模型名 */
    private String embeddingModel = "bge-m3";

    /** 距离阈值：超过此值的检索结果视为不相关，不注入 prompt（L2 距离，越小越相似） */
    private double distanceThreshold = 1.0;
}