package com.vague.core.rag.service;

import com.vague.core.rag.embedding.EmbeddingClient;
import com.vague.core.rag.embedding.RagProperties;
import com.vague.core.rag.entity.ChunkBgeM3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagService {

    private final EmbeddingClient embeddingClient;
    private final ChunkBgeM3Service chunkService;
    private final RagProperties ragProperties;

    public String retrieveContext(String query, List<String> kbIds, int topK) {
        if (kbIds == null || kbIds.isEmpty()) return "";

        float[] queryVector = embeddingClient.embed(query);
        String kbArray = "{" + String.join(",", kbIds) + "}";
        List<ChunkBgeM3> chunks = chunkService.searchByVectorMultiKb(kbArray, vectorToString(queryVector), topK);

        // 过滤掉距离超过阈值的片段（distance 为 null 时保留，避免误过滤）
        return chunks.stream()
                .filter(c -> c.getDistance() == null || c.getDistance() <= ragProperties.getDistanceThreshold())
                .map(ChunkBgeM3::getContent)
                .collect(Collectors.joining("\n---\n"));
    }

    private String vectorToString(float[] vec) {
        return java.util.Arrays.toString(vec).replace(" ", "");
    }
}