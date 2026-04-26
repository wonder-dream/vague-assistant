package com.vague.core.chat.api;

import lombok.Data;

import java.util.Map;

/**
 * DeepSeek tools 字段的工具定义格式
 */
@Data
public class ToolDefinition {
    private String type = "function";
    private FunctionDef function;

    @Data
    public static class FunctionDef {
        private String name;
        private String description;
        private Map<String, Object> parameters;
    }
}
