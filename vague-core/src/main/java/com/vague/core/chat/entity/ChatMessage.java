package com.vague.core.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vague.core.chat.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@TableName("chat_message")
public class ChatMessage extends BaseEntity {
    @TableField("session_id")
    private String sessionId;           // 绑定的会话 UUID

    private String role;                  // user / assistant / system / tool
    private String content;             // 主体内容
    private String metadata;            // JSONB

    public ChatMessage(String sessionId, String role, String content) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
    }
}
