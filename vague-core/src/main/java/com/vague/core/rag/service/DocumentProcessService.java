package com.vague.core.rag.service;

import com.vague.core.rag.chunker.TextChunker;
import com.vague.core.rag.embedding.EmbeddingClient;
import com.vague.core.rag.entity.ChunkBgeM3;
import com.vague.core.rag.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentProcessService {

    private final EmbeddingClient embeddingClient;
    private final ChunkBgeM3Service chunkBgeM3Service;
    private final DocumentService documentService;

    @Async
    public void process(String docId, String kbId, byte[] bytes) {
        try {
            String text = new String(bytes, StandardCharsets.UTF_8);
            List<String> chunks = TextChunker.chunk(text);

            List<ChunkBgeM3> chunkList = new ArrayList<>();
            for (String content : chunks) {
                float[] embedding = embeddingClient.embed(content);
                ChunkBgeM3 chunk = new ChunkBgeM3();
                chunk.setKbId(kbId);
                chunk.setDocId(docId);
                chunk.setContent(content);
                chunk.setEmbedding(embedding);
                chunkList.add(chunk);
            }
            chunkBgeM3Service.saveBatch(chunkList);

            Document doc = documentService.getById(docId);
            doc.setMetadata("{\"status\":\"done\",\"chunkCount\":" + chunkList.size() + "}");
            documentService.updateById(doc);

        } catch (Exception e) {
            Document doc = documentService.getById(docId);
            if (doc != null) {
                doc.setMetadata("{\"status\":\"failed\",\"error\":\"" + e.getMessage() + "\"}");
                documentService.updateById(doc);
            }
        }
    }
}
