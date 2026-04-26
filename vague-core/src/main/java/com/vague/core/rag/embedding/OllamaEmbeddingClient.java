package com.vague.core.rag.embedding;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OllamaEmbeddingClient implements EmbeddingClient {
    private final Gson gson;
    private final RagProperties ragProperties;

    @Override
    public float[] embed(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new float[0];
        }

        RestClient client = RestClient.builder()
                .baseUrl(ragProperties.getOllamaBaseUrl())
                .build();

        EmbeddingRequest request = new EmbeddingRequest(
                ragProperties.getEmbeddingModel(),
                text
        );

        String responseJson = client.post()
                .uri("/api/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(gson.toJson(request))
                .retrieve()
                .body(String.class);

        EmbeddingResponse response = gson.fromJson(responseJson, EmbeddingResponse.class);

        if (response == null || response.getEmbedding() == null) {
            throw new RuntimeException("Ollama embedding 返回为空，请确认模型已加载: ollama pull " + ragProperties.getEmbeddingModel());
        }

        List<Float> embedding = response.getEmbedding();

        // 校验维度（bge-m3 必须是 1024 维，和你数据库 VECTOR(1024) 对应）
        if (embedding.size() != 1024) {
            log.warn("Embedding 维度异常: 期望 1024, 实际 {}", embedding.size());
        }

        float[] result = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            result[i] = embedding.get(i);
        }

        return result;
    }
}
