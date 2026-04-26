package com.vague.core.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vague.core.chat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("agent")
public class Agent extends BaseEntity {
    private String name;                    // Agent 名字
    private String description;             // Agent 描述信息，给用户自己看的

    @TableField("system_prompt")
    private String systemPrompt;            // 系统提示词，role 为 system

    private String model;                   // 选择的模型名称

    /*
     * JSONB 字段：存数组或对象
     * allowed_tools: ["weather", "search"]
     * allowed_kbs: ["kb-001", "kb-002"]
     * chat_options: {"temperature": 0.7, "max_tokens": 2000}
     */
    @TableField("allowed_tools")
    private String allowedTools;            // 可以调用的工具列表
    @TableField("allowed_kbs")
    private String allowedKbs;              // 可以调用给的知识库列表
    @TableField("chat_options")
    private String chatOptions;             // 对话选项
}
