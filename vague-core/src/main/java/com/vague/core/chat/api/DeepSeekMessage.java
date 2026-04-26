package com.vague.core.chat.entity;

import com.vague.core.chat.common.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private Role role;
    private String content;
}
