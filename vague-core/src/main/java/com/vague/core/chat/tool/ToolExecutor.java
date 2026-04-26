package com.vague.core.chat.tool;

import com.vague.core.chat.api.ToolDefinition;

/**
 * 工具执行器接口，每个工具实现一个
 */
public interface ToolExecutor {
    String name();
    ToolDefinition definition();
    String execute(String argumentsJson);
}
