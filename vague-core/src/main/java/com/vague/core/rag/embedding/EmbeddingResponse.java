package com.vague.core.rag.embedding;

import lombok.Data;
import java.util.List;

@Data
public class EmbeddingResponse {
    private List<Float> embedding;
}