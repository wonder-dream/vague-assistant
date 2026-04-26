package com.vague.core.chat.api;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeepSeekMessage {
    private String role;
    private String content;

    // assistant 消息里的工具调用请求
    @SerializedName("tool_calls")
    private List<ToolCall> toolCalls;

    // tool 消息里的调用 ID（对应 assistant 的 tool_calls[i].id）
    @SerializedName("tool_call_id")
    private String toolCallId;

    public DeepSeekMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    @Data
    public static class ToolCall {
        private String id;
        private String type;
        private Function function;

        @Data
        public static class Function {
            private String name;
            private String arguments; // JSON 字符串
        }
    }
}
