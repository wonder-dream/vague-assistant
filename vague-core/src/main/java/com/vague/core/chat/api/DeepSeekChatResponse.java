package com.vague.core.chat.api;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;


@Data
public class DeepSeekChatResponse {
    private String id;
    private String object;
    private Long created;
    private String model;

    // 核心：Choice 数组
    private List<Choice> choices;

    // 用量统计
    private Usage usage;

    @Data
    public static class Choice {
        private Integer index;
        DeepSeekMessage message;
        @SerializedName("finish_reason")
        private String finishReason;
    }

    @Data
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}
