package com.vague.core.chat.api;

import lombok.Data;
import java.util.List;

@Data
public class DeepSeekStreamChunk {
    private String id;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Delta delta;
        private Integer index;
    }

    @Data
    public static class Delta {
        private String role;
        private String content;
    }

    // 辅助方法：取 content
    public String getDeltaContent() {
        if (choices == null || choices.isEmpty()) return null;
        Delta delta = choices.get(0).getDelta();
        if (delta == null) return null;
        return delta.getContent();
    }
}