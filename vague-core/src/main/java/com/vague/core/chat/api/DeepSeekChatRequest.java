package com.vague.core.chat.api;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class DeepSeekChatRequest {
    private String model = "deepseek-chat";
    private List<DeepSeekMessage> messages;
    private Double temperature = 0.7;
    @SerializedName("max_tokens")
    private Integer maxTokens = 2000;
    private Boolean stream = false;
    private List<ToolDefinition> tools;
}
