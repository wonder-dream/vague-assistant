package com.vague.core.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vague.core.chat.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("chat_session")
public class ChatSession extends BaseEntity {
    @TableField("agent_id")
    private String agentId;           // 绑定的 Agent UUID

    private String title;             // 会话标题

    private String metadata;          // JSONB
}
